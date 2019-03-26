package lsieun.utils.radix;

public class BitUtils {
    public static boolean hasBit(int i, int index) {
        if(index > Integer.SIZE || index < 1) return false;
        int rightShift = index - 1;
        int shiftValue = i >> rightShift;
        int andValue = shiftValue & 0x01;
        if(andValue == 1) return true;
        return false;
    }
}
