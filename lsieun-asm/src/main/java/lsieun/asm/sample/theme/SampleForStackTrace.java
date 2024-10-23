package lsieun.asm.sample.theme;

import lsieun.asm.insn.method.AsmInsnUtilsForMethod;

import org.objectweb.asm.ClassVisitor;

public class SampleForStackTrace {
    /**
     * @see AsmInsnUtilsForMethod#addPrintStackFrame(ClassVisitor, String, String)
     */
    public static void printStackWalkerStackFrame(StackWalker.StackFrame frame) {
        String line = String.format("    %s::%s:%s:bci=%s",
                frame.getClassName(),
                frame.getMethodName(),
                frame.getMethodType(),
                frame.getByteCodeIndex());
        System.out.println(line);
    }

    /**
     * @see AsmInsnUtilsForMethod#addPrintStackFrame(ClassVisitor, String, String)
     */
    public static void printStackWalkerStackFrameWithThreadId(StackWalker.StackFrame frame) {
        String line = String.format("    %s::%s:%s:bci=%s:Thread@%s",
                frame.getClassName(),
                frame.getMethodName(),
                frame.getMethodType(),
                frame.getByteCodeIndex(),
                Thread.currentThread().getId());
        System.out.println(line);
    }
}
