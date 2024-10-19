package lsieun.core.match.member;

import lsieun.annotation.mind.todo.ToDo;
import lsieun.core.match.ToSameTypeBiPredicate;
import lsieun.core.match.array.ArrayMatchForMany2Many;
import lsieun.core.match.array.ArrayMatchForMany2One;
import lsieun.core.match.clazz.ClassMatch;
import lsieun.core.match.modifier.ModifierMatcher;
import lsieun.core.match.name.NameMatcher;

import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public class ClassMemberMatcher {
    // region member modifier
    public static <T extends Member> Predicate<T> isPublic() {
        return ModifierMatcher.isPublic(Member::getModifiers);
    }

    public static <T extends Member> Predicate<T> isProtected() {
        return ModifierMatcher.isProtected(Member::getModifiers);
    }

    public static <T extends Member> Predicate<T> isPrivate() {
        return ModifierMatcher.isPrivate(Member::getModifiers);
    }

    public static <T extends Member> Predicate<T> isStatic() {
        return ModifierMatcher.isStatic(Member::getModifiers);
    }

    public static <T extends Member> Predicate<T> isSynthetic() {
        return ModifierMatcher.isSynthetic(Member::getModifiers);
    }

    public static <T extends Member> Predicate<T> modifier(int mod) {
        return ModifierMatcher.modifier(Member::getModifiers, mod);
    }
    // endregion

    // region member name
    public static <T extends Member> Predicate<T> named(String name) {
        return NameMatcher.named(Member::getName, name);
    }

    public static <T extends Member> Predicate<T> nameContains(String name) {
        return NameMatcher.nameContains(Member::getName, name);
    }
    // endregion

    // region executable parameters
    public static <T extends Executable> Predicate<T> paramCount(int num) {
        return m -> ClassMemberParamCountMatch.INSTANCE.test(m, num);
    }

    public static <T extends Executable> Predicate<T> paramTypeContains(Class<?> clazz) {
        ArrayMatchForMany2One<Class<?>> matcher = new ArrayMatchForMany2One<>(ClassMatch.EXACTLY);
        return m -> matcher.test(m.getParameterTypes(), clazz);
    }

    public static <T extends Executable> Predicate<T> paramTypeEquals(Class<?>... clazzArray) {
        ArrayMatchForMany2Many<Class<?>> matcher = new ArrayMatchForMany2Many<>();
        return m -> matcher.test(m.getParameterTypes(), clazzArray);
    }
    // endregion

    // region method return type
    public static Predicate<Method> returnTypeEquals(Class<?> clazz) {
        ToSameTypeBiPredicate<Method, Class<?>> matcher = new ToSameTypeBiPredicate<>(
                Method::getReturnType,
                ClassMatch.EXACTLY
        );

        return m -> matcher.test(m, clazz);
    }

    public static Predicate<Method> returnTypeWithin(Class<?>... clazzArray) {
        ArrayMatchForMany2One<Class<?>> matcher = new ArrayMatchForMany2One<>(ClassMatch.EXACTLY);
        return m -> matcher.test(clazzArray, m.getReturnType());
    }
    // endregion

    @ToDo("进行方法复杂度的比较")
    public static <T extends Executable> int compareParamCount(T one, T another) {
        int count1 = one.getParameterCount();
        int count2 = another.getParameterCount();
        return count1 - count2;
    }

    @ToDo("进行方法复杂度的比较")
    public static <T extends Executable> int compareParamTypeComplex(T one, T another) {
        Class<?>[] newParamTypes = another.getParameterTypes();
        Class<?>[] oldParamTypes = one.getParameterTypes();
        return newParamTypes.length - oldParamTypes.length;
    }
}
