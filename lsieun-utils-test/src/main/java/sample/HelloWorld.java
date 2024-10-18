package sample;

public class HelloWorld {
    public static void printStackWalkerStackFrameWithThreadId(StackWalker.StackFrame frame) {
        String line = String.format("    %s::%s:%s:bci=%s:%s",
                frame.getClassName(),
                frame.getMethodName(),
                frame.getMethodType(),
                frame.getByteCodeIndex(),
                Thread.currentThread().getId());
        System.out.println(line);
    }
}
