package lsieun.utils.core.text;

import static lsieun.utils.core.text.StrConst.EMPTY;
import static lsieun.utils.core.text.StrConst.SPACE;
import static lsieun.utils.core.text.UnicodeUtils.codePoint2Str;
import static lsieun.utils.core.text.UnicodeUtils.isLatin;

public class PanguSpacing {
    public static final int[] SEPARATOR_ARRAY;
    private static final String SEPARATOR_STR = "'<>/\\\" _*、,，.。：:；！？“”（）()[]【】{}《》";
    public static boolean COMPACT_SPACE = false;

    static {
        int length = SEPARATOR_STR.length();
        SEPARATOR_ARRAY = new int[length];
        for (int i = 0; i < length; i++) {
            int codePoint = SEPARATOR_STR.codePointAt(i);
            SEPARATOR_ARRAY[i] = codePoint;
        }
    }


    public static String process(String str) {
        if (str == null) {
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder();

        int length = str.length();
        for (int i = 0; i < length; i++) {
            int curCodePoint = str.codePointAt(i);
            String value = codePoint2Str(curCodePoint);
            if (i == 0) {
                sb.append(value);
                continue;
            }

            int preCodePoint = str.codePointAt(i - 1);

            // 如果是两个相邻的空格，就省略掉
            if (COMPACT_SPACE && bothSpace(preCodePoint, curCodePoint)) {
                continue;
            }

            boolean flag = needSpace(preCodePoint, curCodePoint);
            if (flag) {
                sb.append(SPACE);
            }

            sb.append(value);
        }
        return sb.toString();
    }

    private static boolean bothSpace(int codePoint1, int codePoint2) {
        return codePoint1 == ' ' && codePoint2 == ' ';
    }

    private static boolean needSpace(int codePoint1, int codePoint2) {
//        String val1 = code2Str(codePoint1);
//        String val2 = code2Str(codePoint2);
//        String info = String.format("val1: %s, val2: %s", val1, val2);
//        System.out.println(info);

        boolean flag1 = isLatin(codePoint1);
        boolean flag2 = isLatin(codePoint2);

        boolean quickResult = flag1 ^ flag2;
        if (!quickResult) {
            return false;
        }

        return !isSpecial(codePoint1) && !isSpecial(codePoint2);
    }


    private static boolean isSpecial(int codePoint) {
        for (int item : SEPARATOR_ARRAY) {
            if (item == codePoint) {
                return true;
            }
        }
        return false;
    }

}
