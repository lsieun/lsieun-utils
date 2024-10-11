package lsieun.utils.core.coll;

import java.util.Collection;
import java.util.List;

public class ListUtils {
    public static boolean isNullOrEmpty(final Collection<?> c) {
        return c == null || c.isEmpty();
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
