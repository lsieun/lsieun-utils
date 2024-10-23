package lsieun.asm.insn.code;

import lsieun.asm.core.AsmTypeUtils;
import lsieun.asm.cst.MyAsmConst;
import lsieun.asm.insn.desc.AsmInsnUtilsForDesc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static lsieun.asm.insn.code.AsmInsnUtilsForStackValue.convertValueOnStackToString;
import static org.objectweb.asm.Opcodes.*;

public class AsmInsnUtilsForPrint {
    public static void printMessage(@NotNull MethodVisitor mv, @Nullable String message) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(message == null ? "null" : message);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    /**
     * 注意 printValueOnStack 和 printReturnValue 的区别
     *
     * @see AsmInsnUtilsForMethodReturn#printReturnValue(MethodVisitor, Type)
     */
    public static void printValueOnStack(@NotNull MethodVisitor mv, @NotNull Type t) {
        printValueOnStack(mv, t, "    ");
    }

    public static void printValueOnStack(@NotNull MethodVisitor mv, @NotNull Type t, @NotNull String prefix) {
        if (AsmTypeUtils.hasInvalidValue(t)) {
            return;
        }

        // obj --> str
        // operand stack: obj
        convertValueOnStackToString(mv, t, prefix, null);
        // operand stack: str


        // System.out.println(String)
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        // operand stack: str, System.out
        mv.visitInsn(SWAP);
        // operand stack: System.out, str
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void printValueOnStackV1(@NotNull MethodVisitor mv, @NotNull Type t) {
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
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", MyAsmConst.CONSTRUCTOR_INTERNAL_NAME, "()V", false);
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
