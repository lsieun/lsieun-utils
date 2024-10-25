package lsieun.core.match;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static lsieun.core.match.MatchAsmUtils.*;

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


    static <T> T toTrue(MethodHandles.Lookup lookup, Class<T> samClass) {
        MatchAsmUtils.checkFunctionalInterface(samClass);
        Class<?> clazz = getSamSubClass(lookup, samClass, MAP_OF_BOOL_TRUE, MatchAsmUtils::generateEnumTrue);
        return getEnumInstance(lookup, samClass, clazz);
    }

    static <T> T toFalse(MethodHandles.Lookup lookup, Class<T> samClass) {
        MatchAsmUtils.checkFunctionalInterface(samClass);
        Class<?> clazz = getSamSubClass(lookup, samClass, MAP_OF_BOOL_FALSE, MatchAsmUtils::generateEnumFalse);
        return getEnumInstance(lookup, samClass, clazz);
    }

    static <T> T and(MethodHandles.Lookup lookup, Class<T> samClass, List<T> list) {
        MatchAsmUtils.checkFunctionalInterface(samClass);
        Class<?> clazz = getSamSubClass(lookup, samClass, MAP_OF_LOGIC_AND, MatchAsmUtils::generateLogicAnd);
        return invokeFactoryMethodByList(lookup, samClass, clazz, list);
    }

    static <T> T or(MethodHandles.Lookup lookup, Class<T> samClass, List<T> list) {
        MatchAsmUtils.checkFunctionalInterface(samClass);
        Class<?> clazz = getSamSubClass(lookup, samClass, MAP_OF_LOGIC_OR, MatchAsmUtils::generateLogicOr);
        return invokeFactoryMethodByList(lookup, samClass, clazz, list);
    }

    /**
     * @see Predicate#negate()
     */
    static <T> T negate(MethodHandles.Lookup lookup, Class<T> samClass, T instance) {
        MatchAsmUtils.checkFunctionalInterface(samClass);
        Class<?> clazz = getSamSubClass(lookup, samClass, MAP_OF_LOGIC_NEGATE, MatchAsmUtils::generateLogicNegate);
        return invokeFactoryMethodByObj(lookup, samClass, clazz, instance);
    }
}
