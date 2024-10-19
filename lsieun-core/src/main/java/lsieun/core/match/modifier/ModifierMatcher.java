package lsieun.core.match.modifier;


import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.function.Function;
import java.util.function.Predicate;

public class ModifierMatcher {
    public static <T> Predicate<T> isPublic(Function<T, Integer> func) {
        return item -> {
            int modifier = func.apply(item);
            return Modifier.isPublic(modifier);
        };
    }

    public static <T extends Member> Predicate<T> isProtected(Function<T, Integer> func) {
        return item -> {
            int modifier = func.apply(item);
            return Modifier.isProtected(modifier);
        };
    }

    public static <T extends Member> Predicate<T> isPrivate(Function<T, Integer> func) {
        return item -> {
            int modifier = func.apply(item);
            return Modifier.isPrivate(modifier);
        };
    }

    public static <T extends Member> Predicate<T> isStatic(Function<T, Integer> func) {
        return item -> {
            int modifier = func.apply(item);
            return Modifier.isStatic(modifier);
        };
    }

    public static <T extends Member> Predicate<T> isSynthetic(Function<T, Integer> func) {
        return modifier(func, 0x00001000);
    }

    public static <T> Predicate<T> modifier(Function<T, Integer> func, int mod) {
        return item -> {
            int modifier = func.apply(item);
            return (modifier & mod) != 0;
        };
    }
}
