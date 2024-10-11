package lsieun.utils.core.ds.pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public record Pair<T, S>(T first, S second) {
    public static <T, S, K, V> Map<K, List<V>> groupToMap(List<Pair<T, S>> pairList,
                                                          Function<Pair<T, S>, K> keyFunc,
                                                          Function<Pair<T, S>, V> valueFunc) {
        return pairList.stream()
                .collect(groupingBy(keyFunc, HashMap::new, mapping(valueFunc, toList())));
    }

    public static <T, S, K> Map<K, Long> countToMap(List<Pair<T, S>> pairList, Function<Pair<T, S>, K> keyFunc) {
        return pairList.stream()
                .collect(groupingBy(keyFunc, counting()));
    }

    public static <T, S, K, V> void printGroupMap(List<Pair<T, S>> pairList,
                                                  Function<Pair<T, S>, K> keyFunc,
                                                  Function<Pair<T, S>, V> valueFunc) {
        Map<K, List<V>> map = groupToMap(pairList, keyFunc, valueFunc);
        map.forEach((key, list) -> {
            System.out.println(key);
            for (V value : list) {
                System.out.println("    " + value);
            }
        });
    }

    public static <T, S, K> void printCountMap(List<Pair<T, S>> pairList, Function<Pair<T, S>, K> keyFunc) {
        printCountMap(pairList, keyFunc, 0);
    }

    public static <T, S, K> void printCountMap(List<Pair<T, S>> pairList, Function<Pair<T, S>, K> keyFunc, int min) {
        Map<K, Long> map = countToMap(pairList, keyFunc);
        map.forEach((key, value) -> {
            if (value >= min) {
                String msg = String.format("%s: %d", key, value);
                System.out.println(msg);
            }
        });
    }
}
