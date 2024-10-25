package lsieun.core.match.member;

import lsieun.core.match.LogicAssistant;

import java.lang.invoke.MethodHandles;
import java.util.function.BiPredicate;

public interface ClassMemberParamTypeMatch<T> extends BiPredicate<Class<?>[], T> {
    @Override
    boolean test(Class<?>[] classes, T t);

    LogicAssistant<ClassMemberParamCountMatch> LOGIC = LogicAssistant.of(
            MethodHandles.lookup(), ClassMemberParamCountMatch.class);
}
