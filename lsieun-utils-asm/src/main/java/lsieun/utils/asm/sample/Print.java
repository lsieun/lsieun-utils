package lsieun.utils.asm.sample;

import org.objectweb.asm.ClassVisitor;

public class Print {
    /**
     * @see lsieun.utils.asm.insn.AsmInsnUtilsForMethod#addPrintStackFrame(ClassVisitor, String, String)
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
     * @see lsieun.utils.asm.insn.AsmInsnUtilsForMethod#addPrintStackFrame(ClassVisitor, String, String)
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
