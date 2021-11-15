package lsieun.utils.radix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteUtils {
    public static byte[] fromShort(short x) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.putShort(x);
        return buffer.array();
    }

    public static short toShort(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getShort();
    }

    public static byte[] fromInt(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(x);
        return buffer.array();
    }

    public static int toInt(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getInt();
    }

    public static byte[] fromLong(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static long toLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static byte[] fromFloat(float x) {
        ByteBuffer buffer = ByteBuffer.allocate(Float.BYTES);
        buffer.putFloat(x);
        return buffer.array();
    }

    public static float toFloat(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Float.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getFloat();
    }

    public static byte[] fromDouble(double x) {
        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        buffer.putDouble(x);
        return buffer.array();
    }

    public static double toDouble(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getDouble();
    }

    public static byte[] fromUtf8(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static String toUtf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] intToBytes(final int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i >> 24);
        bytes[1] = (byte) ((i >> 16) & 0xFF);
        bytes[2] = (byte) ((i >> 8) & 0xFF);
        bytes[3] = (byte) (i & 0xFF);
        return bytes;
    }

    public static int bytesToInt(final byte[] bytes) {
        return bytesToInt(bytes, 0);
    }

    public static int bytesToInt(final byte[] bytes, final int defaultValue) {
        if (bytes == null || bytes.length < 1) return defaultValue;

        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) + (b & 0xFF);
        }
        return value;
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
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
