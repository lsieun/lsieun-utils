package lsieun.base.reflect.clazz;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassUtils {
    // java.lang.invoke.InnerClassLambdaMetafactory的lambdaClassName字段
    public static boolean isLambdaClass(Class<?> clazz) {
        return clazz.getName().contains("$$Lambda$");
    }

    public static Method findSingleAbstractMethod(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Method samMethod = null;
        int samCount = 0;
        for (Method m : methods) {
            int modifiers = m.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                samMethod = m;
                samCount++;
            }
        }

        if(samCount == 0) {
            throw new IllegalArgumentException("No SAM method found for " + clazz);
        }
        else if (samCount == 1) {
            return samMethod;
        }
        else {
            throw new IllegalArgumentException("Multiple abstract methods found for " + clazz);
        }

    }
}
