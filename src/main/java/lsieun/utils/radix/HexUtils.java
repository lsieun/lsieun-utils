package lsieun.utils.radix;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HexUtils {
    public static final Map<Character, Integer> hex2IntMap;

    static {
        hex2IntMap = new HashMap<Character, Integer>();
        hex2IntMap.put('0', Integer.valueOf(0));
        hex2IntMap.put('1', Integer.valueOf(1));
        hex2IntMap.put('2', Integer.valueOf(2));
        hex2IntMap.put('3', Integer.valueOf(3));
        hex2IntMap.put('4', Integer.valueOf(4));
        hex2IntMap.put('5', Integer.valueOf(5));
        hex2IntMap.put('6', Integer.valueOf(6));
        hex2IntMap.put('7', Integer.valueOf(7));
        hex2IntMap.put('8', Integer.valueOf(8));
        hex2IntMap.put('9', Integer.valueOf(9));
        hex2IntMap.put('A', Integer.valueOf(10));
        hex2IntMap.put('B', Integer.valueOf(11));
        hex2IntMap.put('C', Integer.valueOf(12));
        hex2IntMap.put('D', Integer.valueOf(13));
        hex2IntMap.put('E', Integer.valueOf(14));
        hex2IntMap.put('F', Integer.valueOf(15));
    }


    public static int toInt(String hexCode) {
        int base = 16;
        int sum = 0;

        for(int i=0; i<hexCode.length(); i++) {
            char ch = hexCode.charAt(i);
            Integer value = hex2IntMap.get(Character.toUpperCase(ch));

            sum = sum * base + value;
        }
        return sum;
    }

    public static float toFloat(String hexCode) {
        Long i = Long.parseLong(hexCode, 16);
        Float f = Float.intBitsToFloat(i.intValue());
        return f;
    }

    public static double toDouble(String hexCode) {
        Long i = Long.parseLong(hexCode, 16);
        Double value = Double.longBitsToDouble(i);
        return value;
    }

    public static String toUtf8(String hexCode) {
        hexCode = hexCode.toLowerCase();
        byte[] bytes = toBytes(hexCode);
        String str = new String(bytes, StandardCharsets.UTF_8);
        return str;
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

    private static byte toByte(char c) {
        byte b = (byte) "0123456789abcdef".indexOf(c);
        return b;
    }

    public static String fromBytes(byte[] bytes) {
        if(bytes == null || bytes.length < 1) return null;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            sb.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).substring(1,3));
        }
        return sb.toString().toUpperCase();
    }

    public static String fromBytes(List<Byte> list) {
        if(list == null || list.size() < 1) return null;
        int size = list.size();
        byte[] bytes = new byte[size];
        for(int i=0; i<size; i++) {
            Byte b = list.get(i);
            bytes[i] = b;
        }
        return fromBytes(bytes);
    }
}
