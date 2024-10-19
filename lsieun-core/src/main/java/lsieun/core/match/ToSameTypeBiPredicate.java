package lsieun.core.match;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class ToSameTypeBiPredicate<T, S> implements BiPredicate<T, S> {
    private final Function<T, S> func;
    private final BiPredicate<? super S, ? super S> predicate;
    private final MatchDirection direction;

    public ToSameTypeBiPredicate(Function<T, S> func,
                                 BiPredicate<? super S, ? super S> predicate) {
        this(func, predicate, MatchDirection.FORWARD);
    }

    public ToSameTypeBiPredicate(Function<T, S> func,
                                 BiPredicate<? super S, ? super S> predicate,
                                 MatchDirection direction) {
        this.func = func;
        this.predicate = predicate;
        this.direction = direction;
    }

    @Override
    public boolean test(T t, S s) {
        S val = func.apply(t);
        if (direction == MatchDirection.FORWARD) {
            return predicate.test(val, s);
        }
        else if (direction == MatchDirection.BACKWARD) {
            return predicate.test(s, val);
        }
        else {
            assert false : "you should not be here";
            return false;
        }
    }

}
