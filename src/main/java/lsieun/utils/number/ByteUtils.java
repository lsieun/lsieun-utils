package lsieun.utils.number;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ByteUtils {

    public static byte[] fromShort(final short s) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((s >> 8) & 0xFF);
        bytes[1] = (byte) (s & 0xFF);
        return bytes;
    }

    public static short toShort(final byte[] bytes) {
        return (short) toInt(bytes);
    }


    public static byte[] fromInt(final int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i >> 24);
        bytes[1] = (byte) ((i >> 16) & 0xFF);
        bytes[2] = (byte) ((i >> 8) & 0xFF);
        bytes[3] = (byte) (i & 0xFF);
        return bytes;
    }

    public static int toInt(final byte[] bytes) {
        return toInt(bytes, 0);
    }

    public static int toInt(final byte[] bytes, final int defaultValue) {
        if (bytes == null || bytes.length < 1) return defaultValue;

        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) + (b & 0xFF);
        }
        return value;
    }

    public static byte[] fromLong(final long longValue) {
        byte[] result = new byte[8];
        for (int i = 0; i < Long.BYTES; i++) {
            result[i] = (byte) ((longValue >> (i * 8)) & 0xFF);
        }
        return result;
    }

    public static long toLong(final byte[] bytes) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static byte[] fromFloat(final float floatValue) {
        int intValue = Float.floatToIntBits(floatValue);
        return fromInt(intValue);
    }

    public static float toFloat(final byte[] bytes) {
        int intValue = toInt(bytes);
        return Float.intBitsToFloat(intValue);
    }

    public static byte[] fromDouble(final double doubleValue) {
        long longValue = Double.doubleToLongBits(doubleValue);
        return fromLong(longValue);
    }

    public static double toDouble(final byte[] bytes) {
        long longValue = toLong(bytes);
        return Double.longBitsToDouble(longValue);
    }

    public static byte[] fromUtf8(final String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static String toUtf8(final byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] merge(byte[]... bytesArray) {
        if (bytesArray == null || bytesArray.length < 1) return null;

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        for (byte[] bytes : bytesArray) {
            if (bytes != null && bytes.length > 0) {
                try {
                    bao.write(bytes);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bao.toByteArray();
    }

    public static byte[] concatenate(byte[] bytes1, byte[] bytes2) {
        int len1 = bytes1.length;
        int len2 = bytes2.length;

        byte[] result_bytes = new byte[len1 + len2];

        System.arraycopy(bytes1, 0, result_bytes, 0, len1);
        System.arraycopy(bytes2, 0, result_bytes, len1, len2);

        return result_bytes;
    }

    public static byte[] concatenate(byte[] bytes1, byte[] bytes2, byte[] bytes3) {
        int len1 = bytes1.length;
        int len2 = bytes2.length;
        int len3 = bytes3.length;

        byte[] result_bytes = new byte[len1 + len2 + len3];

        System.arraycopy(bytes1, 0, result_bytes, 0, len1);
        System.arraycopy(bytes2, 0, result_bytes, len1, len2);
        System.arraycopy(bytes3, 0, result_bytes, len1 + len2, len3);

        return result_bytes;
    }

    public static String toBinary(byte[] bytes) {
        if (bytes == null) return "";

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            toBinary(sb, b);
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String toBinary(byte b) {
        StringBuilder sb = new StringBuilder();
        toBinary(sb, b);
        return sb.toString();
    }

    private static void toBinary(StringBuilder sb, byte b) {
        for (int i = 7; i >= 0; i--) {
            int val = (b >> i) & 0x01;
            sb.append(val);
        }
    }
}