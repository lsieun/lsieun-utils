package lsieun.utils.core.text;

public class UnicodeUtils {
    public static String codePoint2Str(int codePoint) {
        char[] charArray = Character.toChars(codePoint);
        return new String(charArray);
    }

    public static boolean isLatin(int codePoint) {
        return codePoint < 256;
    }
}
