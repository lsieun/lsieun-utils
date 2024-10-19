package lsieun.base.text;

import static lsieun.base.text.StrConst.EMPTY;
import static lsieun.base.text.StrConst.SPACE;
import static lsieun.base.text.UnicodeUtils.codePoint2Str;

public class CommentSpacing {
    public static String process(String str) {
        if (str == null) {
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder();

        int length = str.length();
        for (int i = 0; i < length; i++) {
            int curCodePoint = str.codePointAt(i);
            String value = codePoint2Str(curCodePoint);
            if (i < 2) {
                sb.append(value);
                continue;
            }

            int minus2CodePoint = str.codePointAt(i - 2);
            int minus1CodePoint = str.codePointAt(i - 1);

            // 如果是两个相邻的空格，就省略掉
            if (minus2CodePoint == '/' && minus1CodePoint == '/') {
                if (curCodePoint != '/' && curCodePoint != ' ') {
                    sb.append(SPACE);
                }
            }

            sb.append(value);
        }
        return sb.toString();
    }
}
