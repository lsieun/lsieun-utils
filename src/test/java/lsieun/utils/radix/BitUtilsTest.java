package lsieun.utils.radix;

import org.junit.Test;

public class BitUtilsTest {

    @Test
    public void hasBit() {
        int i = 0x0001;
        System.out.println(BitUtils.hasBit(i, 1));
        System.out.println(BitUtils.hasBit(i, 2));
    }
}