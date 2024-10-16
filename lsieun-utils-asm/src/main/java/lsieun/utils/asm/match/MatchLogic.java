package lsieun.utils.asm.match;

import lsieun.utils.core.reflect.clazz.ClassUtils;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static lsieun.utils.asm.match.MatchAsmUtils.*;

/**
 * <pre>
 *                             ┌─── toTrue()
 *               ┌─── bool ────┤
 *               │             └─── toFalse()
 * MatchLogic ───┤
 *               │             ┌─── and()
 *               │             │
 *               └─── logic ───┼─── or()
 *                             │
 *                             └─── negate()
 * </pre>
 *
 * @see MatchAsmUtils
 */
public interface MatchLogic {
    Map<Class<?>, Class<?>> MAP_OF_BOOL_TRUE = new ConcurrentHashMap<>();
    Map<Class<?>, Class<?>> MAP_OF_BOOL_FALSE = new ConcurrentHashMap<>();
    Map<Class<?>, Class<?>> MAP_OF_LOGIC_AND = new ConcurrentHashMap<>();
    Map<Class<?>, Class<?>> MAP_OF_LOGIC_OR = new ConcurrentHashMap<>();
    Map<Class<?>, Class<?>> MAP_OF_LOGIC_NEGATE = new ConcurrentHashMap<>();


    static <T> T toTrue(Class<T> samClass) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return toTrue(lookup, samClass);
    }

    static <T> T toTrue(MethodHandles.Lookup lookup, Class<T> samClass) {
        ClassUtils.checkFunctionalInterface(samClass);
        Class<?> clazz = getSamSubClass(lookup, samClass, MAP_OF_BOOL_TRUE, MatchAsmUtils::generateEnumTrue);
        return getEnumInstance(lookup, samClass, clazz);
    }

    static <T> T toFalse(Class<T> samClass) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return toFalse(lookup, samClass);
    }

    static <T> T toFalse(MethodHandles.Lookup lookup, Class<T> samClass) {
        ClassUtils.checkFunctionalInterface(samClass);
        Class<?> clazz = getSamSubClass(lookup, samClass, MAP_OF_BOOL_FALSE, MatchAsmUtils::generateEnumFalse);
        return getEnumInstance(lookup, samClass, clazz);
    }

    static <T> T and(Class<T> samClass, List<T> list) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return and(lookup, samClass, list);
    }

    static <T> T and(MethodHandles.Lookup lookup, Class<T> samClass, List<T> list) {
        ClassUtils.checkFunctionalInterface(samClass);
        Class<?> clazz = getSamSubClass(lookup, samClass, MAP_OF_LOGIC_AND, MatchAsmUtils::generateLogicAnd);
        return invokeFactoryMethodByList(lookup, samClass, clazz, list);
    }

    static <T> T or(Class<T> samClass, List<T> list) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return or(lookup, samClass, list);
    }

    static <T> T or(MethodHandles.Lookup lookup, Class<T> samClass, List<T> list) {
        ClassUtils.checkFunctionalInterface(samClass);
        Class<?> clazz = getSamSubClass(lookup, samClass, MAP_OF_LOGIC_OR, MatchAsmUtils::generateLogicOr);
        return invokeFactoryMethodByList(lookup, samClass, clazz, list);
    }

    static <T> T negate(Class<T> samClass, T instance) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return negate(lookup, samClass, instance);
    }

    static <T> T negate(MethodHandles.Lookup lookup, Class<T> samClass, T instance) {
        ClassUtils.checkFunctionalInterface(samClass);
        Class<?> clazz = getSamSubClass(lookup, samClass, MAP_OF_LOGIC_NEGATE, MatchAsmUtils::generateLogicNegate);
        return invokeFactoryMethodByObj(lookup, samClass, clazz, instance);
    }
}
