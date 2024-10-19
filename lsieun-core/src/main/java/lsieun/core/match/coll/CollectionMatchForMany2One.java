package lsieun.core.match.coll;

import lsieun.core.match.MatchDirection;

import java.util.Collection;
import java.util.function.BiPredicate;

public class CollectionMatchForMany2One<T> implements BiPredicate<Collection<T>, T> {
    private final BiPredicate<? super T, ? super T> matcher;
    private final MatchDirection direction;

    public CollectionMatchForMany2One(BiPredicate<? super T, ? super T> matcher) {
        this(matcher, MatchDirection.FORWARD);
    }

    private CollectionMatchForMany2One(BiPredicate<? super T, ? super T> matcher, MatchDirection direction) {
        this.matcher = matcher;
        this.direction = direction;
    }

    public static <S> CollectionMatchForMany2One<S> of(BiPredicate<? super S, ? super S> matcher,
                                                       MatchDirection direction) {
        return new CollectionMatchForMany2One<>(matcher, direction);
    }

    @Override
    public boolean test(Collection<T> coll, T target) {
        for (T item : coll) {
            boolean flag = direction == MatchDirection.BACKWARD ?
                    matcher.test(target, item) :
                    matcher.test(item, target);
            if (flag) {
                return true;
            }
        }

        return false;
    }
}
