package lsieun.core.match;

import lsieun.base.coll.ListUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * <h2>Usage</h2>
 * <code>LogicAssistant&lt;TextMatch&gt; LOGIC = LogicAssistant.of(MethodHandles.lookup(), TextMatch.class);</code>
 * <h2>MindMap</h2>
 * <pre>
 *                                                   ┌─── lookup
 *                   ┌─── static ───────┼─── of() ───┤
 *                   │                               └─── clazz
 *                   │
 * LogicAssistant ───┤                                ┌─── alwaysTrue()
 *                   │                  ┌─── bool ────┤
 *                   │                  │             └─── alwaysFalse()
 *                   └─── non-static ───┤
 *                                      │             ┌─── and()
 *                                      │             │
 *                                      └─── logic ───┼─── or()
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

    public T alwaysTrue() {
        return MatchLogic.toTrue(lookup, clazz);
    }

    public T alwaysFalse() {
        return MatchLogic.toFalse(lookup, clazz);
    }

    @SafeVarargs
    public final T and(@NotNull T first, T... more) {
        if (more == null || more.length == 0) {
            return first;
        }

        List<T> list = ListUtils.toList(first, more);
        return and(list);
    }

    @SafeVarargs
    public final T and(boolean defaultValue, T... logicArray) {
        if (logicArray == null || logicArray.length == 0) {
            return defaultValue ? alwaysTrue() : alwaysFalse();
        }

        List<T> list = ListUtils.toList(logicArray);
        if (ListUtils.isNullOrEmpty(list)) {
            return defaultValue ? alwaysTrue() : alwaysFalse();
        }

        return and(list);
    }

    public T and(List<T> list) {
        if (ListUtils.isNullOrEmpty(list)) {
            throw new IllegalArgumentException("list is empty");
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        return MatchLogic.and(lookup, clazz, list);
    }

    @SafeVarargs
    public final T or(@NotNull T first, T... more) {
        if (more == null || more.length == 0) {
            return first;
        }

        List<T> list = ListUtils.toList(first, more);
        return or(list);
    }

    @SafeVarargs
    public final T or(boolean defaultValue, T... logicArray) {
        if (logicArray == null || logicArray.length == 0) {
            return defaultValue ? alwaysTrue() : alwaysFalse();
        }

        List<T> list = ListUtils.toList(logicArray);
        if (ListUtils.isNullOrEmpty(list)) {
            return defaultValue ? alwaysTrue() : alwaysFalse();
        }

        return or(list);
    }

    public T or(List<T> list) {
        if (ListUtils.isNullOrEmpty(list)) {
            throw new IllegalArgumentException("list is empty");
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        return MatchLogic.or(lookup, clazz, list);
    }

    public T not(@NotNull T instance) {
        return MatchLogic.negate(lookup, clazz, instance);
    }

    public static <R> LogicAssistant<R> of(MethodHandles.Lookup lookup, Class<R> clazz) {
        return new LogicAssistant<>(lookup, clazz);
    }
}
