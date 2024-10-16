package lsieun.utils.asm.format;

public class MethodInfoFormat {
    public static String methodEnter(String owner, int methodAccess, String methodName, String methodDesc) {
        return String.format("%s::%s:%s %s", owner, methodName, methodDesc, ">".repeat(81));
    }

    public static String methodExit(String owner, int methodAccess, String methodName, String methodDesc) {
        return String.format("%s::%s:%s %s", owner, methodName, methodDesc, "<".repeat(81));
    }

    public static String methodExitReturn(String owner, int methodAccess, String methodName, String methodDesc) {
        return String.format("[RETURN] %s::%s:%s %s", owner, methodName, methodDesc, "<".repeat(81));
    }

    public static String methodExitThrown(String owner, int methodAccess, String methodName, String methodDesc) {
        return String.format("[THROWN] %s::%s:%s %s", owner, methodName, methodDesc, "<".repeat(81));
    }
}
