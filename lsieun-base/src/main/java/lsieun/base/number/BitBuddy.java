package lsieun.base.number;

/**
 * <pre>
 *      byte: 0000 0000
 *     index: 7654 3210
 * </pre>
 */
public class BitBuddy {
    public static boolean hasBit(byte b, int index) {
        int num = b & 0xFF;
        int bitmask = 1 << index;
        return (num & bitmask) != 0;
    }

    public static byte setBit(byte b, int index) {
        int num = b & 0xFF;
        int bitmask = 1 << index;
        return (byte) (num | bitmask);
    }

    public static byte clearBit(byte b, int index) {
        int num = b & 0xFF;
        int bitmask = 1 << index;
        return (byte) (num & (~bitmask));
    }

    public static byte toggleBit(byte b, int index) {
        int num = b & 0xFF;
        int mask = 1 << index;
        return (byte) (num ^ mask);
    }

    public static byte toggleBit(byte b, int... indexArray) {
        int num = b & 0xFF;
        for (int index : indexArray) {
            int mask = 1 << index;
            num = num ^ mask;
        }
        return (byte) num;
    }

    public static byte swapBit(byte b, int index1, int index2) {
        boolean isSet1 = hasBit(b, index1);
        boolean isSet2 = hasBit(b, index2);
        if (isSet1 ^ isSet2) {
            return toggleBit(b, index1, index2);
        }
        else {
            return b;
        }
    }

    public static byte swap(byte b) {
        byte b1 = swapBit(b, 0, 7);
        byte b2 = swapBit(b1, 1, 6);
        byte b3 = swapBit(b2, 2, 5);
        return swapBit(b3, 3, 4);
    }

    public static byte swapByIndexArray(byte b, int... indexArray) {
        int length = indexArray.length;
        int count = length / 2;
        for (int i = 0; i < count; i++) {
            int index = indexArray[2 * i];
            int nextIndex = indexArray[2 * i + 1];

            b = swapBit(b, index, nextIndex);
        }
        return b;
    }

    public static byte move(byte b, int... indexArray) {
        byte newB = b;
        int length = indexArray.length;
        for (int i = 0; i < length; i++) {
            int index = indexArray[i];
            int nextIndex = indexArray[(i + 1) % length];

//            String msg = String.format("b = %d, i = %d, index = %d, nextIndex = %d", b, i, index, nextIndex);
//            System.out.println(msg);

            boolean hasBitAtIndex = hasBit(b, index);
            if (hasBitAtIndex) {
                newB = setBit(newB, nextIndex);
            }
            else {
                newB = clearBit(newB, nextIndex);
            }
        }

        return newB;
    }

    public static String format(byte byteValue) {
        int length = Byte.SIZE;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int val = ((byte) (byteValue << i) < 0) ? 0x31 : 0x30;
            bytes[i] = (byte) val;
        }
        return new String(bytes);
    }

    public static int parse(String str) {
        int length = str.length();
        int result = 0;
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            if (ch != '1' && ch != '0') {
                continue;
            }
            int bit = (ch == '1' ? 1 : 0);
            result = (result << 1) | bit;
        }
        return result;
    }
}