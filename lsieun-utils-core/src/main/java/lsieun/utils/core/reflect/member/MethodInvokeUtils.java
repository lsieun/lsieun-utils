package lsieun.utils.core.reflect.member;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <ul>
 *     <li>learn-java-reflect</li>
 *     <li>box-drawing-utils</li>
 *     <li>learn-java-bytebuddy</li>
 * </ul>
 */
public class MethodInvokeUtils {
    public static final String[] NAMES = {
            "Tom", "Jerry", "Spike",
            "唐玄藏", "孙悟空", "猪悟能", "沙悟净",
            "魏国信陵君魏无忌", "赵国平原君赵胜", "楚国春申君黄歇", "齐国孟尝君田文",
    };

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

    public static void invokeMethodListWithObj(List<Method> methodList, List<Object> list) throws Exception {
        for (Method m : methodList) {
            Class<?>[] parameterTypes = m.getParameterTypes();
            Object[] randomMethodArgs = createRandomMethodArgs(parameterTypes);
            for (Object obj : list) {
                Object result = m.invoke(obj, randomMethodArgs);
                System.out.println(result);
            }
        }
    }

    private static Object[] createRandomMethodArgs(Class<?>[] parameterTypes) {
        int length = parameterTypes.length;
        Object[] args = new Object[length];

        for (int i = 0; i < length; i++) {
            Class<?> type = parameterTypes[i];
            if (type.isPrimitive()) {
                Object val;
                if (type == boolean.class) {
                    val = Boolean.TRUE;
                }
                else if (type == byte.class) {
                    val = (byte) (1 + Math.random() * 10);
                }
                else if (type == short.class) {
                    val = (short) (11 + Math.random() * 10);
                }
                else if (type == char.class) {
                    val = (char) ('A' + Math.random() * 26);
                }
                else if (type == int.class) {
                    val = (int) (21 + Math.random() * 10);
                }
                else if (type == float.class) {
                    val = (float) (31 + Math.random() * 10);
                }
                else if (type == long.class) {
                    val = (long) (41 + Math.random() * 10);
                }
                else if (type == double.class) {
                    val = 51 + Math.random() * 10;
                }
                else {
                    val = null;
                }
                args[i] = val;
            }
            else if (type == String.class) {
                int num = NAMES.length;
                int index = (int) (num * Math.random());
                args[i] = NAMES[index];
            }
            else if (type == Date.class) {
                args[i] = new Date();
            }
            else if (type == LocalDate.class) {
                args[i] = LocalDate.now();
            }
            else {
                args[i] = null;
            }
        }

        return args;
    }
}
