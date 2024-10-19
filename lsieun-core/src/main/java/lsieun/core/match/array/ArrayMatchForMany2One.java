package lsieun.core.match.array;

import lsieun.core.match.MatchDirection;

import java.util.function.BiPredicate;

public class ArrayMatchForMany2One<T> implements BiPredicate<T[], T> {
    private final BiPredicate<? super T, ? super T> matcher;
    private final MatchDirection direction;

    public ArrayMatchForMany2One(BiPredicate<? super T, ? super T> matcher) {
        this(matcher, MatchDirection.FORWARD);
    }

    public ArrayMatchForMany2One(BiPredicate<? super T, ? super T> matcher,
                                 MatchDirection direction) {
        this.matcher = matcher;
        this.direction = direction;
    }

    @Override
    public boolean test(T[] array, T target) {
        for (T item : array) {
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
