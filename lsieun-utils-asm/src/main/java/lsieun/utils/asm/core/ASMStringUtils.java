package lsieun.utils.asm.core;

public class ASMStringUtils {
    public static String getClassMemberInfo(String name, String descriptor) {
        return String.format("%s:%s", name, descriptor);
    }

    public static String getClassMemberInfo(String internalName, String methodName, String methodDesc) {
        return String.format("%s.class %s:%s", internalName, methodName, methodDesc);
    }
}
