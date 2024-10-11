package lsieun.utils.asm.code;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class CodeMethodUtils {
    public static void addPrintStackFrame(ClassVisitor cv) {
        if (cv != null) {
            MethodVisitor mv = cv.visitMethod(ACC_STATIC, "printStackFrame", "(Ljava/lang/StackWalker$StackFrame;)V", null, null);
            if (mv != null) {
                mv.visitCode();
                mv.visitLdcInsn("    %s::%s:%s:bci=%s");
                mv.visitInsn(ICONST_4);
                mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "getClassName", "()Ljava/lang/String;", true);
                mv.visitInsn(AASTORE);
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "getMethodName", "()Ljava/lang/String;", true);
                mv.visitInsn(AASTORE);
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_2);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "getDescriptor", "()Ljava/lang/String;", true);
                mv.visitInsn(AASTORE);
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_3);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "getByteCodeIndex", "()I", true);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                mv.visitInsn(AASTORE);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 1);
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(5, 2);
                mv.visitEnd();
            }
        }
    }
}
