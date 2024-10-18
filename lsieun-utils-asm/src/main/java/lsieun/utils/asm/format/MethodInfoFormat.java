package lsieun.utils.asm.format;

public class MethodInfoFormat {
    public static final int REPEAT_COUNT = 27;

    public static String methodEnter(String owner, int methodAccess, String methodName, String methodDesc) {
        return String.format("[METHOD  ENTER] %s::%s:%s %s", owner, methodName, methodDesc, ">".repeat(REPEAT_COUNT));
    }

    public static String methodExit(String owner, int methodAccess, String methodName, String methodDesc) {
        return String.format("[METHOD   EXIT] %s::%s:%s %s", owner, methodName, methodDesc, "<".repeat(REPEAT_COUNT));
    }

    public static String methodExitReturn(String owner, int methodAccess, String methodName, String methodDesc) {
        return String.format("[METHOD RETURN] %s::%s:%s %s", owner, methodName, methodDesc, "<".repeat(REPEAT_COUNT));
    }

    public static String methodExitThrown(String owner, int methodAccess, String methodName, String methodDesc) {
        return String.format("[METHOD THROWN] %s::%s:%s %s", owner, methodName, methodDesc, "<".repeat(REPEAT_COUNT));
    }
}
