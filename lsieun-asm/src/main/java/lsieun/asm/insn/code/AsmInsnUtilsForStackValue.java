package lsieun.asm.insn.code;

import lsieun.asm.core.AsmTypeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.*;
import static lsieun.asm.cst.MyAsmConst.StringBuilderConst.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * <pre>
 *                                                                 ┌─── convertPrimitiveValueOnStackToString()
 *                                                                 │
 *                         ┌─── convertValueOnStackToString() ─────┼─── convertArrayValueOnStackToString()
 *                         │                                       │
 * AsmInsnUtilsForValue ───┤                                       └─── convertObjectValueOnStackToString()
 *                         │
 *                         └─── convertValueOnStackToTypeName()
 * </pre>
 */
public class AsmInsnUtilsForStackValue {
    public static void convertValueOnStackToString(@NotNull MethodVisitor mv,
                                                   @NotNull Type t,
                                                   @Nullable String prefix,
                                                   @Nullable String suffix) {
        if (AsmTypeUtils.hasInvalidValue(t)) {
            return;
        }


        // obj --> str: (1) str = String.valueOf(obj), (2) str = Arrays.toString(array), (3) ...
        // operand stack: obj
        convertValueOnStackToString(mv, t);
        // operand stack: str

        if (isEmpty(prefix) && isEmpty(suffix)) {
            return;
        }


        // StringBuilder builder = new StringBuilder();
        // operand stack: str
        mv.visitTypeInsn(NEW, STRING_BUILDER_INTERNAL_NAME);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, STRING_BUILDER_INTERNAL_NAME,
                INIT_METHOD_NAME, INIT_METHOD_DEFAULT_DESC, false);
        // operand stack: str, builder

        // builder.append(prefix);
        if (prefix != null) {
            // operand stack: str, builder
            mv.visitLdcInsn(prefix);
            // operand stack: str, builder, prefix
            mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME,
                    APPEND_METHOD_NAME, APPEND_STRING_METHOD_DESC, false);
            // operand stack: str, builder
        }


        // builder.append(str);
        // operand stack: str, builder
        mv.visitInsn(SWAP);
        // operand stack: builder, str
        mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME,
                APPEND_METHOD_NAME, APPEND_STRING_METHOD_DESC, false);
        // operand stack: builder


        // builder.append(suffix);
        if (suffix != null) {
            // operand stack: builder
            mv.visitLdcInsn(suffix);
            // operand stack: builder, suffix
            mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME,
                    APPEND_METHOD_NAME, APPEND_STRING_METHOD_DESC, false);
            // operand stack: builder
        }


        // builder.toString()
        // operand stack: builder
        mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME,
                TO_STRING_METHOD_NAME, TO_STRING_METHOD_DESC, false);
        // operand stack: str
    }

    private static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.isEmpty();
    }

    /**
     * <pre>
     *     public static valueOf:(Ljava/lang/Object;)Ljava/lang/String;
     *     public static valueOf:()Ljava/lang/String;
     * </pre>
     */
    public static void convertValueOnStackToString(@NotNull MethodVisitor mv, @NotNull Type t) {
        if (AsmTypeUtils.hasInvalidValue(t)) {
            return;
        }


        // String - do nothing, just return
        if (Type.getType(String.class).equals(t)) {
            return;
        }

        int sort = t.getSort();
        // primitive
        if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
            convertPrimitiveValueOnStackToString(mv, t);
        }
        else if (sort == Type.ARRAY) {
            convertArrayValueOnStackToString(mv, t);
        }
        else {
            convertObjectValueOnStackToString(mv, t);
        }
    }

    /**
     * @see String#valueOf(boolean)
     * @see String#valueOf(char)
     * @see String#valueOf(int)
     * @see String#valueOf(long)
     * @see String#valueOf(float)
     * @see String#valueOf(double)
     */
    private static void convertPrimitiveValueOnStackToString(@NotNull MethodVisitor mv, @NotNull Type t) {
        AsmTypeUtils.requiresPrimitiveValue(t);

        int sort = t.getSort();
        String descriptor = (sort >= Type.BYTE && sort <= Type.INT) ?
                Type.INT_TYPE.getDescriptor() :
                t.getDescriptor();
        String methodDesc = String.format("(%s)Ljava/lang/String;", descriptor);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", methodDesc, false);
    }

    private static void convertArrayValueOnStackToString(@NotNull MethodVisitor mv, @NotNull Type t) {
        AsmTypeUtils.requiresArrayValue(t);

        int dimensions = t.getDimensions();
        Type elementType = t.getElementType();
        int elementTypeSort = elementType.getSort();
        if (dimensions == 1) {
            if (elementTypeSort == Type.CHAR) {
                // String.valueOf(char[])
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
                        "([C)Ljava/lang/String;", false);
            }
            else if (elementTypeSort >= Type.BOOLEAN && elementTypeSort <= Type.DOUBLE) {
                // Arrays.toString()
                String descriptor = t.getDescriptor();
                String methodDesc = String.format("(%s)Ljava/lang/String;", descriptor);
                mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString", methodDesc, false);
            }
            else {
                // Arrays.toString()
                mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString",
                        "([Ljava/lang/Object;)Ljava/lang/String;", false);
            }
        }
        else {
            convertObjectValueOnStackToString(mv, t);
        }
    }

    /**
     * @see String#valueOf(Object)
     */
    private static void convertObjectValueOnStackToString(@NotNull MethodVisitor mv, @NotNull Type t) {
        AsmTypeUtils.requiresObjectValue(t);

        // String.valueOf()
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
                "(Ljava/lang/Object;)Ljava/lang/String;", false);
    }

    public static void convertValueOnStackToTypeName(@NotNull MethodVisitor mv) {
        // operand stack: [obj]
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        // operand stack: [class]
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getTypeName", "()Ljava/lang/String;", false);
        // operand stack: [str]
    }
}
