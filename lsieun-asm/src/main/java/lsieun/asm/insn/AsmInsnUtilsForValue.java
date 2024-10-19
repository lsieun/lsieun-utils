package lsieun.asm.insn;

import jakarta.annotation.Nonnull;
import lsieun.asm.core.AsmTypeBuddy;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

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
public class AsmInsnUtilsForValue {
    /**
     * <pre>
     *     public static valueOf:(Ljava/lang/Object;)Ljava/lang/String;
     *     public static valueOf:()Ljava/lang/String;
     * </pre>
     */
    public static void convertValueOnStackToString(@Nonnull MethodVisitor mv, @Nonnull Type t) {
        AsmTypeBuddy.requiresValidValue(t);


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
    public static void convertPrimitiveValueOnStackToString(@Nonnull MethodVisitor mv, @Nonnull Type t) {
        AsmTypeBuddy.requiresPrimitiveValue(t);

        int sort = t.getSort();
        String descriptor = (sort >= Type.BYTE && sort <= Type.INT) ?
                Type.INT_TYPE.getDescriptor() :
                t.getDescriptor();
        String methodDesc = String.format("(%s)Ljava/lang/String;", descriptor);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", methodDesc, false);
    }

    public static void convertArrayValueOnStackToString(@Nonnull MethodVisitor mv, @Nonnull Type t) {
        AsmTypeBuddy.requiresArrayValue(t);

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
    public static void convertObjectValueOnStackToString(@Nonnull MethodVisitor mv, @Nonnull Type t) {
        AsmTypeBuddy.requiresObjectValue(t);

        // String.valueOf()
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
                "(Ljava/lang/Object;)Ljava/lang/String;", false);
    }

    public static void convertValueOnStackToTypeName(@Nonnull MethodVisitor mv) {
        // operand stack: [obj]
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        // operand stack: [class]
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getTypeName", "()Ljava/lang/String;", false);
        // operand stack: [str]
    }
}
