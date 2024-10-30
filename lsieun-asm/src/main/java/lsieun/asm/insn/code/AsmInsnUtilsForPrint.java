package lsieun.asm.insn.code;

import lsieun.asm.core.AsmTypeUtils;
import lsieun.asm.insn.desc.AsmInsnUtilsForDesc;
import lsieun.base.text.StrConst;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.INIT_METHOD_DEFAULT_DESC;
import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.INIT_METHOD_NAME;
import static lsieun.asm.insn.code.AsmInsnUtilsForStackValue.convertValueOnStackToString;
import static lsieun.asm.insn.opcode.OpcodeForStack.dupValueOnStack;
import static org.objectweb.asm.Opcodes.*;

public class AsmInsnUtilsForPrint {
    public static void printMessage(@NotNull final MethodVisitor mv,
                                    @Nullable final String message) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(message == null ? "null" : message);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    /**
     * 注意 printValueOnStack 和 printReturnValue 的区别
     *
     * @see AsmInsnUtilsForMethodReturn#printReturnValue(MethodVisitor, Type)
     */
    public static void printValueOnStackWithPrefix(@NotNull final MethodVisitor mv,
                                                   @NotNull final Type t,
                                                   @Range(from = 0, to = 20) final int spaceCount) {
        printValueOnStackWithPrefix(mv, t, StrConst.SPACE.repeat(spaceCount));
    }

    public static void printValueOnStackWithPrefix(@NotNull final MethodVisitor mv,
                                                   @NotNull final Type t,
                                                   @NotNull final String prefix) {
        printValueOnStackWithPrefixAndSuffix(mv, t, prefix, null);
    }

    public static void printValueOnStackWithPrefixAndSuffix(@NotNull final MethodVisitor mv,
                                                            @NotNull final Type t,
                                                            @Nullable final String prefix,
                                                            @Nullable final String suffix) {
        if (AsmTypeUtils.hasInvalidValue(t)) {
            return;
        }

        // obj --> str
        // operand stack: obj
        convertValueOnStackToString(mv, t, prefix, suffix);
        // operand stack: str


        // System.out.println(String)
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        // operand stack: str, System.out
        mv.visitInsn(SWAP);
        // operand stack: System.out, str
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void dupAndPrintValueOnStack(@NotNull final MethodVisitor mv,
                                               @NotNull final Type t,
                                               @Nullable final String prefix,
                                               @Nullable final String suffix) {
        dupValueOnStack(mv, t);
        printValueOnStackWithPrefixAndSuffix(mv, t, prefix, suffix);
    }

    public static void printValueOnStackV1(@NotNull final MethodVisitor mv,
                                           @NotNull final Type t) {
        int size = t.getSize();
        if (size == 0) {
            // size = 0, t = VOID_TYPE
            return;
        }

        // operand stack: val
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        // operand stack: val, System.out

        // swap
        if (size == 1) {
            mv.visitInsn(SWAP);
        }
        else if (size == 2) {
            mv.visitInsn(DUP_X2);
            mv.visitInsn(POP);
        }
        else {
            assert false : "should not be here";
        }
        // operand stack: System.out, val

        // StringBuilder builder = new StringBuilder();
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", INIT_METHOD_NAME, INIT_METHOD_DEFAULT_DESC, false);
        // operand stack: System.out, val, builder

        // builder.append("    ");
        mv.visitLdcInsn("    ");
        // operand stack: System.out, val, builder, str
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        // operand stack: System.out, val, builder

        // swap
        if (size == 1) {
            mv.visitInsn(SWAP);
        }
        else {
            mv.visitInsn(DUP_X2);
            mv.visitInsn(POP);
        }
        // operand stack: System.out, builder, val

        // Arrays.toString(String[])
        String paramDesc = t.getDescriptor();
        if (paramDesc.startsWith("[") && paramDesc.endsWith(";")) {
//        if ("[Ljava/lang/String;".equals(paramDesc)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString", "([Ljava/lang/Object;)Ljava/lang/String;", false);
        }

        // builder.append(val);
        String descriptor = AsmInsnUtilsForDesc.getDescForStringBuilderAppend(t);

        // operand stack: System.out, builder, val
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", descriptor, false);
        // operand stack: System.out, builder
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        // operand stack: System.out, str
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
