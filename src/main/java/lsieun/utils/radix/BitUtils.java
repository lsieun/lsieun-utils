package lsieun.utils.radix;

public class BitUtils {
    public static boolean hasBit(int i, int index) {
        if(index > Integer.SIZE || index < 1) return false;
        int rightShift = index - 1;
        int shiftValue = i >> rightShift;
        int andValue = shiftValue & 0x01;
        return andValue == 1;
    }

    /**
     * This does not return a 1 for a 1 bit; it just returns non-zero
     */
    public static int get_bit(byte[] array, int bit) {
        return (array[bit / 8] & 0xFF) & (0x80 >> (bit % 8));
    }

    public static void set_bit(byte[] array, int bit) {
        int val = (array[bit / 8] & 0xFF) | (0x80 >> (bit % 8));
        array[bit / 8] = (byte) val;
    }

    public static void clear_bit(byte[] array, int bit) {
        int val = (array[bit / 8] & 0xFF) & ~(0x80 >> (bit % 8));
        array[bit / 8] = (byte) val;
    }
}
