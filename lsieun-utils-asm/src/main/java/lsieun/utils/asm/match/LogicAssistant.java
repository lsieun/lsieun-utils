package lsieun.utils.asm.match;

import jakarta.annotation.Nonnull;
import lsieun.utils.core.coll.ListUtils;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *                                                                         ┌─── lookup
 *                                                       ┌─── of() ────────┤
 *                   ┌─── static ───────┼─── instance ───┤                 └─── clazz
 *                   │                                   │
 *                   │                                   └─── builder() ───┼─── withLookup() ───┼─── withClass()
 * LogicAssistant ───┤
 *                   │                                ┌─── and()
 *                   │                                │
 *                   └─── non-static ───┼─── logic ───┼─── or()
 *                                                    │
 *                                                    └─── not()
 * </pre>
 */
public final class LogicAssistant<T> {
    private final MethodHandles.Lookup lookup;
    private final Class<T> clazz;

    private LogicAssistant(MethodHandles.Lookup lookup, Class<T> clazz) {
        this.lookup = lookup;
        this.clazz = clazz;
    }

    @SafeVarargs
    public final T and(@Nonnull T first, T... more) {
        Objects.requireNonNull(first);

        List<T> list = ListUtils.toList(first, more);
        return MatchLogic.and(lookup, clazz, list);
    }

    public T and(List<T> list) {
        return MatchLogic.and(lookup, clazz, list);
    }

    @SafeVarargs
    public final T or(@Nonnull T first, T... more) {
        Objects.requireNonNull(first);

        List<T> list = ListUtils.toList(first, more);
        return or(list);
    }

    public T or(List<T> list) {
        return MatchLogic.or(lookup, clazz, list);
    }

    public T not(T instance) {
        return MatchLogic.negate(lookup, clazz, instance);
    }

    public static <R> AddClass<R> builder() {
        return clazz -> lookup -> new LogicAssistant<R>(lookup, clazz);
    }

    public interface AddClass<R> {
        AddLookup<R> withClass(Class<R> clazz);
    }

    public interface AddLookup<R> {
        LogicAssistant<R> withLookup(MethodHandles.Lookup lookup);
    }

    public static <R> LogicAssistant<R> of(MethodHandles.Lookup lookup, Class<R> clazz) {
        return new LogicAssistant<R>(lookup, clazz);
    }
}
