package lsieun.utils.match.text;

import lsieun.utils.annotation.type.UtilityClass;
import lsieun.utils.match.MatchDirection;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;

@UtilityClass
public class TextMatchForOne2Many {
    public static boolean within(String one, String... array) {
        Objects.requireNonNull(one);
        Objects.requireNonNull(array);

        return match(
                MatchDirection.FORWARD,
                TextMatchForOne2One.EQUALS_FULLY,
                one, array
        );

    }

    public static <T> boolean match(MatchDirection direction,
                                    BiPredicate<T, T> predicate,
                                    T one, T... array) {
        Objects.requireNonNull(direction);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(one);
        Objects.requireNonNull(array);

        for (T element : array) {
            boolean flag = direction == MatchDirection.FORWARD ?
                    predicate.test(one, element) :
                    predicate.test(element, one);
            ;
            if (flag) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean match(MatchDirection direction,
                                    BiPredicate<T, T> predicate,
                                    T one, Collection<T> coll) {
        Objects.requireNonNull(direction);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(one);
        Objects.requireNonNull(coll);

        for (T element : coll) {
            boolean flag = direction == MatchDirection.FORWARD ?
                    predicate.test(one, element) :
                    predicate.test(element, one);
            ;
            if (flag) {
                return true;
            }
        }
        return false;
    }
}
