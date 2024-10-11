package lsieun.utils.core.reflect.member;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <ul>
 *     <li>learn-java-reflect</li>
 *     <li>box-drawing-utils</li>
 * </ul>
 */
public class MemberFindUtils {
    public static List<Field> getFieldList(Class<?> clazz) {
        return getMemberList(clazz, Class::getDeclaredFields);
    }

    public static List<Constructor<?>> getConstructorList(Class<?> clazz) {
        return getMemberList(clazz, Class::getDeclaredConstructors);
    }

    public static List<Method> getMethodList(Class<?> clazz) {
        return getMemberList(clazz, Class::getDeclaredMethods);
    }

    public static <T extends Member> List<T> getMemberList(Class<?> clazz, Function<Class<?>, T[]> memberFunc) {
        // members
        T[] members = memberFunc.apply(clazz);
        if (members.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.asList(members);
    }

    public static <T extends Member> List<T> filter(List<T> memberList, Predicate<? super T> predicate) {
        return memberList.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
}
