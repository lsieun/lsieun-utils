package lsieun.utils.core.reflect.clazz;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class ClassUtils {
    // java.lang.invoke.InnerClassLambdaMetafactory的lambdaClassName字段
    public static boolean isLambdaClass(Class<?> clazz) {
        return clazz.getName().contains("$$Lambda$");
    }

    public static void checkFunctionalInterface(Class<?> clazz) {
        Objects.requireNonNull(clazz);

        // (1) check type: interface
        if (!clazz.isInterface()) {
            String msg = String.format("The class %s is not an interface", clazz.getTypeName());
            throw new IllegalArgumentException(msg);
        }

        // (2) check annotation
        if (clazz.getAnnotation(FunctionalInterface.class) == null) {
            String msg = String.format(
                    "The class %s does not have the @FunctionalInterface annotation",
                    clazz.getTypeName()
            );
            throw new IllegalArgumentException(msg);
        }

        // (3) check method return: boolean
        Method samMethod = findSingleAbstractMethod(clazz);
        Class<?> returnType = samMethod.getReturnType();
        if (returnType != boolean.class) {
            String msg = String.format(
                    "The %s.%s(...) does not return boolean.",
                    clazz.getTypeName(),
                    samMethod.getName()
            );
            throw new IllegalArgumentException(msg);
        }
    }

    public static Method findSingleAbstractMethod(Class<?> clazz) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            int modifiers = method.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                return method;
            }
        }
        throw new IllegalArgumentException("No method found for " + clazz);
    }
}
