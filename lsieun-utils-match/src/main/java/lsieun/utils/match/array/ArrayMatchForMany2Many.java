package lsieun.utils.match.array;

import java.util.Arrays;
import java.util.function.BiPredicate;

public class ArrayMatchForMany2Many<T> implements BiPredicate<T[], T[]> {
    @Override
    public boolean test(T[] ts, T[] ts2) {
        return Arrays.equals(ts, ts2);
    }
}
