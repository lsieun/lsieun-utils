package lsieun.base.coll;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ListUtils {
    private ListUtils() {
        throw new UnsupportedOperationException("This class is a utility class and not supposed to be instantiated");
    }

    public static boolean isNullOrEmpty(final Collection<?> c) {
        return c == null || c.isEmpty();
    }

    @SafeVarargs
    public static <T> List<T> toList(T... array) {
        if (array == null || array.length == 0) {
            return Collections.emptyList();
        }

        List<T> list = new ArrayList<T>();
        int length = array.length;
        for (int i = 0; i < length; i++) {
            T item = array[i];
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    @SafeVarargs
    public static <T> List<T> toList(@NotNull T first, T... rest) {
        List<T> list = new ArrayList<T>();
        list.add(first);

        if (rest != null && rest.length > 0) {
            int length = rest.length;
            for (int i = 0; i < length; i++) {
                T item = rest[i];
                if (item != null) {
                    list.add(item);
                }
            }
        }

        return list;
    }

    public static void print(List<?> list) {
        int size = list.size();
        String title = String.format("LIST SIZE: %d", size);
        System.out.println(title);
        for (int i = 0; i < size; i++) {
            Object obj = list.get(i);
            String info = String.format("    %03d: %s", (i + 1), obj);
            System.out.println(info);
        }
    }
}
