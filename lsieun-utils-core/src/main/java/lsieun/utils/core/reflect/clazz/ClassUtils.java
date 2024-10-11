package lsieun.utils.core.reflect.clazz;

public class ClassUtils {
    // java.lang.invoke.InnerClassLambdaMetafactory的lambdaClassName字段
    public static boolean isLambdaClass(Class<?> clazz) {
        return clazz.getName().contains("$$Lambda$");
    }
}
