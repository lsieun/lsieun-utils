package lsieun.utils;

import java.util.List;

public class RegexUtils {
    public static void filter(List<String> list, String regex) {
        if (list == null || list.size() < 1) return;
        if (regex == null || "".equals(regex)) return;

        int size = list.size();
        for(int index = size -1; index >=0; index--) {
            String item = list.get(index);
            if (!matches(item, regex)) {
                list.remove(index);
            }
        }
    }

    public static void filter(List<String> list, String[] regex_array) {
        if (list == null || list.size() < 1) return;
        if (regex_array == null || regex_array.length < 1) return;

        int size = list.size();
        for(int index = size -1; index >=0; index--) {
            String item = list.get(index);
            if (!matches(item, regex_array)) {
                list.remove(index);
            }
        }
    }

    public static boolean matches(String str, String regex) {
        if (str == null) return false;
        if (regex == null) return true;

        if (str.matches(regex)) return true;
        return false;
    }

    public static boolean matches(String str, String[] regex_array) {
        if (str == null) return false;
        if (regex_array == null || regex_array.length < 1) return true;

        for (String regex : regex_array) {
            if (matches(str, regex)) {
                return true;
            }
        }
        return false;
    }
}
