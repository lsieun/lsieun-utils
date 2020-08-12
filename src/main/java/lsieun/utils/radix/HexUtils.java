package lsieun.utils.radix;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 规则一：方法命名方式为fromXXX和toXXX
 * 规则二：使用小写的abcdef
 */
public class HexUtils {
    static final String hexString = "0123456789abcdef";
    static final char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static int toInt(String hexCode) {
        int base = 16;
        int sum = 0;

        for (int i = 0; i < hexCode.length(); i++) {
            char ch = hexCode.charAt(i);
            int value = hexString.indexOf(ch);

            sum = sum * base + value;
        }
        return sum;
    }

    public static float toFloat(String hexCode) {
        Long longValue = Long.parseLong(hexCode, 16);
        return Float.intBitsToFloat(longValue.intValue());
    }

    public static double toDouble(String hexCode) {
        Long longValue = Long.parseLong(hexCode, 16);
        return Double.longBitsToDouble(longValue);
    }

    public static String toUtf8(String hexCode) {
        hexCode = hexCode.toLowerCase();
        byte[] bytes = toBytes(hexCode);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] toBytes(String hexCode) {
        hexCode = hexCode.toLowerCase();
        int len = (hexCode.length() / 2);
        byte[] result = new byte[len];
        char[] array = hexCode.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(array[pos]) << 4 | toByte(array[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char ch) {
        byte b = (byte) hexString.indexOf(ch);
        return b;
    }

    public static String fromBytes(byte[] bytes) {
        if (bytes == null || bytes.length < 1) return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            sb.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static String fromBytes(List<Byte> list) {
        if (list == null || list.size() < 1) return null;
        int size = list.size();
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            Byte b = list.get(i);
            bytes[i] = b;
        }
        return fromBytes(bytes);
    }

    public static String fromChars(final char[] chars) {
        if (chars == null || chars.length < 1) return "";
        List<String> list = new ArrayList<>();
        for (char ch : chars) {
            list.add(charToHex(ch));
        }
        return reverse2String(list);
    }

    public static String fromInt(int value) {
        if (value == 0) return "00";
        List<String> list = new ArrayList<>();
        while (value != 0) {
            byte b = (byte) (value & 0xFF);
            String hex = byteToHex(b);
            list.add(hex);
            value = value >>> 8;
        }


        return reverse2String(list);
    }

    private static String reverse2String(List<String> list) {
        if (list == null || list.size() < 1) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = list.size() - 1; i >= 0; i--) {
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    public static String byteToHex(byte b) {
        char[] chars = {hexDigit[(b >>> 4) & 0x0F], hexDigit[b & 0x0F]};
        return new String(chars);
    }

    public static String charToHex(char ch) {
        byte hi = (byte) (ch >>> 8);
        byte lo = (byte) (ch & 0xff);
        return byteToHex(hi) + byteToHex(lo);
    }

    public static String intToHex(int i) {
        char hi = (char) (i >>> 16);
        char lo = (char) (i & 0xffff);
        return charToHex(hi) + charToHex(lo);
    }

    public static String getPrettyFormat(String hexCode) {
        if (hexCode == null || hexCode.length() < 1) return null;

        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        int count = 0;
        for (int i = 0; i < hexCode.length(); i++) {
            char ch = hexCode.charAt(i);
            count++;
            fm.format("%c", ch);
            if (count % 2 == 0) {
                fm.format(" ");
            }
            if (count % 32 == 0) {
                fm.format("%n");
            }
        }
        return sb.toString();
    }

    public static String format(byte[] bytes, HexFormat format) {
        String separator = format.separator;
        int bytes_column = format.columns;
        return format(bytes, separator, bytes_column);
    }

    public static String format(byte[] bytes, String separator, int bytes_column) {
        if (bytes == null || bytes.length < 1) return "";

        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);

        int length = bytes.length;
        for (int i = 0; i < length - 1; i++) {
            int val = bytes[i] & 0xFF;
            fm.format("%02X", val);
            if (bytes_column > 0 && (i + 1) % bytes_column == 0) {
                fm.format("%n");
            } else {
                fm.format("%s", separator);
            }
        }
        {
            int val = bytes[length - 1] & 0xFF;
            fm.format("%02X", val);
        }

        return sb.toString();
    }

    public static byte[] parse(String str, HexFormat format) {
        char[] chars = format.separator.toCharArray();
        return parse(str, chars);
    }

    public static byte[] parse(String str, char[] chars) {
        int length = str.length();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            if (is_in(ch, chars)) {
                continue;
            }
            sb.append(ch);
        }
        String hex_str = sb.toString();
        return parse(hex_str);
    }

    public static boolean is_in(char ch, char[] chars) {
        for (char item : chars) {
            if (item == ch) {
                return true;
            }
        }
        return false;
    }

    public static byte[] parse(String hex_str) {
        int length = hex_str.length();
        int count = length / 2;

        byte[] bytes = new byte[count];
        for (int i = 0; i < count; i++) {
            String item = hex_str.substring(2 * i, 2 * i + 2);
            int val = Integer.parseInt(item, 16);
            bytes[i] = (byte) val;
        }
        return bytes;
    }

    public static String toHex(byte[] bytes) {
        return format(bytes, HexFormat.FORMAT_FF_FF);
    }
}
