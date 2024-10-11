package lsieun.utils.core.text;

import java.util.List;

public class RegexUtils {
    public static void filter(List<String> list, String regex) {
        if (list == null || list.size() < 1) {
            return;
        }
        if (regex == null || "".equals(regex)) {
            return;
        }

        int size = list.size();
        for (int index = size - 1; index >= 0; index--) {
            String item = list.get(index);
            if (!matches(item, regex)) {
                list.remove(index);
            }
        }
    }

    public static void filter(List<String> list, String[] regexArray) {
        if (list == null || list.size() < 1) {
            return;
        }
        if (regexArray == null || regexArray.length < 1) {
            return;
        }

        int size = list.size();
        for (int index = size - 1; index >= 0; index--) {
            String item = list.get(index);
            if (!matches(item, regexArray)) {
                list.remove(index);
            }
        }
    }

    public static boolean matches(String str, String regex) {
        if (str == null) {
            return false;
        }
        if (regex == null) {
            return true;
        }

        return str.matches(regex);
    }

    public static boolean matches(String str, String[] regexArray) {
        return matches(str, regexArray, true);
    }

    public static boolean matches(String str, String[] regexArray, boolean defaultValue) {
        if (str == null) {
            return false;
        }
        if (regexArray == null || regexArray.length < 1) {
            return defaultValue;
        }

        for (String regex : regexArray) {
            if (matches(str, regex)) {
                return true;
            }
        }
        return false;
    }
}
