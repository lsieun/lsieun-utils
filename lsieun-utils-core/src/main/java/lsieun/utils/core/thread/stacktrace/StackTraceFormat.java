package lsieun.utils.core.thread.stacktrace;

public class StackTraceFormat {
    public static final String FORMAT = "    %s::%s:%s:bci=%s";

    public static String formatTitle(String className, String methodName, String methodDesc) {
        return String.format("[Stack Trace] %s:%s:%s", className, methodName, methodDesc);
    }

    public static String format(String className, String methodName, String methodDesc, int byteCodeIndex) {
        return String.format(FORMAT, className, methodName, methodDesc, byteCodeIndex);
    }
}
