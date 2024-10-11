package lsieun.utils.core.reflect.member;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <ul>
 *     <li>learn-java-reflect</li>
 *     <li>box-drawing-utils</li>
 *     <li>learn-java-bytebuddy</li>
 * </ul>
 */
public class MethodInvokeUtils {
    public static Object invokeOneMethodWithArgs(String className, String methodName, Object... args) throws Exception {
        return invokeOneMethodWithArgs(Class.forName(className), methodName, args);
    }

    public static Object invokeOneMethodWithArgs(Class<?> clazz, String methodName, Object... args) throws Exception {
        Method[] methodArray = clazz.getDeclaredMethods();
        Method candidate = null;
        for (Method method : methodArray) {
            if (method.getName().endsWith(methodName)) {
                candidate = method;
                break;
            }
        }

        if (candidate == null) {
            String message = String.format("can not find target method: %s from %s", methodName, clazz.getName());
            System.out.println(message);
            return null;
        }

        int modifiers = candidate.getModifiers();
        Class<?> returnType = candidate.getReturnType();

        Object result;
        if (Modifier.isStatic(modifiers)) {
            result = candidate.invoke(args);
        }
        else {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            Object instance = constructor.newInstance();
            result = candidate.invoke(instance, args);
        }

        if (returnType != void.class) {
            System.out.println(result);
        }
        return result;
    }


    private static void printMethodAndArgs(Method method, Object[] args) {
        String methodInfo = getMethodInfo(method);
        String argsInfo = Arrays.toString(args);
        String line = String.format("%s --> %s", methodInfo, argsInfo);
        System.out.println(line);
    }

    private static String getMethodInfo(Method method) {
        int modifiers = method.getModifiers();
        String methodName = method.getName();
        String modifierStr = Modifier.toString(modifiers);
        int parameterCount = method.getParameterCount();
        Class<?> returnType = method.getReturnType();
        Class<?>[] parameterTypes = method.getParameterTypes();

        String returnString = returnType.getSimpleName();
        List<String> parameterList = new ArrayList<>();
        for (int i = 0; i < parameterCount; i++) {
            String str = parameterTypes[i].getSimpleName();
            parameterList.add(str);
        }
        String parameterString = String.join(", ", parameterList);

        return String.format("%n[Method] %s %s %s(%s)", modifierStr, returnString, methodName, parameterString);
    }


}
