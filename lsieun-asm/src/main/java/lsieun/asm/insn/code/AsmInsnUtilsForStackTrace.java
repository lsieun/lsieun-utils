package lsieun.asm.insn.code;

import lsieun.asm.cst.MyAsmConst;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class AsmInsnUtilsForStackTrace {
    public static void printStackTrace(@NotNull MethodVisitor mv, @NotNull String msg) {
        mv.visitTypeInsn(NEW, "java/lang/Exception");
        mv.visitInsn(DUP);
        mv.visitLdcInsn(msg);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Exception", MyAsmConst.CONSTRUCTOR_INTERNAL_NAME, "(Ljava/lang/String;)V", false);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "(Ljava/io/PrintStream;)V", false);
    }

    public static void printStackTraceSinceJava9(@NotNull MethodVisitor mv, @NotNull String owner,
                                                 @NotNull String methodName, @NotNull String methodDesc) {

        mv.visitMethodInsn(INVOKESTATIC, "java/lang/StackWalker", "getInstance", "()Ljava/lang/StackWalker;", false);
        mv.visitInvokeDynamicInsn("accept", "()Ljava/util/function/Consumer;",
                new Handle(
                        Opcodes.H_INVOKESTATIC,
                        "java/lang/invoke/LambdaMetafactory", "metafactory",
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                        false
                ),
                new Object[]{
                        Type.getType("(Ljava/lang/Object;)V"),
                        new Handle(Opcodes.H_INVOKESTATIC, owner, methodName, methodDesc, false),
                        Type.getType(methodDesc)
                }
        );
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackWalker", "forEach", "(Ljava/util/function/Consumer;)V", false);
    }
}
