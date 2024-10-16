package lsieun.utils.core.operators.logical;

import java.util.List;
import java.util.function.Function;

public class And {
    public static <T> T of(Class<?> clazz, List<T> list, Function<? super T, Boolean> func) {
        return null;
    }

    public static void main(String[] args) {
        Class<?> clazz = Object.class;
        Module baseModule = clazz.getModule();
        System.out.println(baseModule);

        Module currentModule = And.class.getModule();
        System.out.println(currentModule);
        Module newModule = baseModule.addOpens("jdk.internal.org.objectweb.asm", currentModule);
        System.out.println(newModule);
    }

    static Module getModule(Class<?> clazz) {
        return clazz.getModule();
    }
}
