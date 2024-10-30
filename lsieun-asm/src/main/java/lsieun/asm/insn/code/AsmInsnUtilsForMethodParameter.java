package lsieun.asm.insn.code;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


import static lsieun.asm.cst.MyAsmConst.MethodNameAndDescConst.INIT_METHOD_NAME;
import static lsieun.asm.insn.code.AsmInsnUtilsForPrint.printValueOnStackWithPrefix;
import static org.objectweb.asm.Opcodes.*;

public class AsmInsnUtilsForMethodParameter {
    public static void printParameters(@NotNull MethodVisitor mv, int methodAccess, @NotNull String methodDesc) {
        int slotIndex = (methodAccess & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
        Type methodType = Type.getMethodType(methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        for (Type t : argumentTypes) {
            printParameter(mv, slotIndex, t);

            int size = t.getSize();
            slotIndex += size;
        }
    }

    public static void printParameter(@NotNull MethodVisitor mv, int slotIndex, @NotNull Type t) {
        // load variable
        int opcode = t.getOpcode(Opcodes.ILOAD);
        mv.visitVarInsn(opcode, slotIndex);

        // prefix
        String prefix = String.format("    slots[%02d] - %s - ", slotIndex, t.getClassName());

        // print
        printValueOnStackWithPrefix(mv, t, prefix);
    }

    public static void printParameterV1(@NotNull MethodVisitor mv, int slotIndex, @NotNull Type t) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", INIT_METHOD_NAME, "()V", false);
        mv.visitLdcInsn("    ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        if (slotIndex >= 0 && slotIndex <= 5) {
            mv.visitInsn(ICONST_0 + slotIndex);
        }
        else {
            mv.visitIntInsn(BIPUSH, slotIndex);
        }

        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn(": ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

        // load variable
        int opcode = t.getOpcode(Opcodes.ILOAD);
        mv.visitVarInsn(opcode, slotIndex);

        // Arrays.toString(String[])
        String paramDesc = t.getDescriptor();
        if (paramDesc.startsWith("[") && paramDesc.endsWith(";")) {
//        if ("[Ljava/lang/String;".equals(paramDesc)) {
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "toString", "([Ljava/lang/Object;)Ljava/lang/String;", false);
        }

        // descriptor
        int sort = t.getSort();
        String appendDesc;
        if (sort == Type.SHORT) {
            appendDesc = "(I)Ljava/lang/StringBuilder;";
        }
        else if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
            appendDesc = "(" + paramDesc + ")Ljava/lang/StringBuilder;";
        }
        else {
            appendDesc = "(Ljava/lang/Object;)Ljava/lang/StringBuilder;";
        }

        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", appendDesc, false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
