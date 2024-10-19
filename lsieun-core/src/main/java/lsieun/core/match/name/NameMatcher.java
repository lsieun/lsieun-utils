package lsieun.core.match.name;

import lsieun.core.match.array.ArrayMatchForMany2One;
import lsieun.core.match.coll.CollectionMatchForMany2One;
import lsieun.core.match.text.TextMatchForOne2One;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class NameMatcher {
    public static <T> Predicate<T> named(Function<T, String> func, String name) {
        return item -> {
            String actualName = func.apply(item);
            return TextMatchForOne2One.EQUALS_FULLY.test(actualName, name);
        };
    }

    public static <T> Predicate<T> namedIgnoreCase(Function<T, String> func, String name) {
        return item -> {
            String actualName = func.apply(item);
            return TextMatchForOne2One.EQUALS_FULLY_IGNORE_CASE.test(actualName, name);
        };
    }

    public static <T> Predicate<T> nameStartsWith(Function<T, String> func, String name) {
        return item -> {
            String actualName = func.apply(item);
            return TextMatchForOne2One.STARTS_WITH.test(actualName, name);
        };
    }

    public static <T> Predicate<T> nameStartsWithIgnoreCase(Function<T, String> func, String name) {
        return item -> {
            String actualName = func.apply(item);
            return TextMatchForOne2One.STARTS_WITH_IGNORE_CASE.test(actualName, name);
        };
    }

    public static <T> Predicate<T> nameEndsWith(Function<T, String> func, String name) {
        return item -> {
            String actualName = func.apply(item);
            return TextMatchForOne2One.ENDS_WITH.test(actualName, name);
        };
    }

    public static <T> Predicate<T> nameEndsWithIgnoreCase(Function<T, String> func, String name) {
        return item -> {
            String actualName = func.apply(item);
            return TextMatchForOne2One.ENDS_WITH_IGNORE_CASE.test(actualName, name);
        };
    }

    public static <T> Predicate<T> nameContains(Function<T, String> func, String name) {
        return item -> {
            String actualName = func.apply(item);
            return TextMatchForOne2One.CONTAINS.test(actualName, name);
        };
    }

    public static <T> Predicate<T> nameContainsIgnoreCase(Function<T, String> func, String name) {
        return item -> {
            String actualName = func.apply(item);
            return TextMatchForOne2One.CONTAINS_IGNORE_CASE.test(actualName, name);
        };
    }

    public static <T> Predicate<T> nameByRegex(Function<T, String> func, String regex) {
        return item -> {
            String actualName = func.apply(item);
            return TextMatchForOne2One.REGEX.test(actualName, regex);
        };
    }

    public static <T> Predicate<T> nameWithin(Function<T, String> func,
                                              String... array) {
        return nameCompareWithArray(func, TextMatchForOne2One.EQUALS_FULLY_IGNORE_CASE, array);
    }

    public static <T> Predicate<T> nameCompareWithArray(Function<T, String> func,
                                                        BiPredicate<String, String> predicate,
                                                        String... array) {
        ArrayMatchForMany2One<String> matcher = new ArrayMatchForMany2One<>(predicate);
        return item -> {
            String value = func.apply(item);
            return matcher.test(array, value);
        };
    }

    public static <T> Predicate<T> nameCompareWithCollection(Function<T, String> func,
                                                             BiPredicate<String, String> predicate,
                                                             Collection<String> coll) {
        CollectionMatchForMany2One<String> matcher = new CollectionMatchForMany2One<>(predicate);
        return item -> {
            String value = func.apply(item);
            return matcher.test(coll, value);
        };
    }
}
