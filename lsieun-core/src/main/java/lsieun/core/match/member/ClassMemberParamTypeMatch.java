package lsieun.core.match.member;

import java.util.function.BiPredicate;

public class ClassMemberParamTypeMatch<T> implements BiPredicate<Class<?>[], T> {
//    private final BiPredicate<T, Class<?>>

    @Override
    public boolean test(Class<?>[] classes, T t) {
        return false;
    }
}
