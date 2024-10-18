package lsieun.utils.asm.insn;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class AsmInsnUtilsForMethod {
    /**
     * @see lsieun.utils.asm.sample.Print#printStackWalkerStackFrame(StackWalker.StackFrame)
     */
    public static void addPrintStackFrame(ClassVisitor cv, String methodName, String methodDesc) {
        if (cv != null) {
            MethodVisitor mv = cv.visitMethod(ACC_PUBLIC | ACC_STATIC, methodName, methodDesc, null, null);
            if (mv != null) {
                mv.visitCode();
                mv.visitLdcInsn("    %s::%s:%s:bci=%s:Thread@%s");

                // array = new Object[5];
                mv.visitInsn(ICONST_5);
                mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

                // array[0]
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "getClassName", "()Ljava/lang/String;", true);
                mv.visitInsn(AASTORE);

                // array[1]
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "getMethodName", "()Ljava/lang/String;", true);
                mv.visitInsn(AASTORE);

                // array[2]
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_2);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "getDescriptor", "()Ljava/lang/String;", true);
                mv.visitInsn(AASTORE);

                // array[3]
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_3);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "getByteCodeIndex", "()I", true);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                mv.visitInsn(AASTORE);

                // array[4]
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_4);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                mv.visitInsn(AASTORE);

                // String.format()
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 1);

                // System.out.println()
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(6, 2);
                mv.visitEnd();
            }
        }
    }
}
