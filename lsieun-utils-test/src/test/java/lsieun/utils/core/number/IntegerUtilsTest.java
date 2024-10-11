package lsieun.utils.core.number;

import org.junit.jupiter.api.Test;

public class IntegerUtilsTest {
    @Test
    public void testInt() {
        test(0);
        test(1);
        test(-1);
        test(127);
        test(128);
        test(-128);
        test(65535);
        test(-65536);
        test(Integer.MAX_VALUE);
        test(Integer.MIN_VALUE);
        test(0xCAFEBABE);
    }

    private void test(int i) {
        System.out.println("i = " + i);
        byte[] bytes1 = IntegerUtils.toBytes(i);
        System.out.println("IntegerUtils: " + HexUtils.fromBytes(bytes1));
        byte[] bytes2 = ByteUtils.fromInt(i);
        System.out.println("ByteUtils: " + HexUtils.fromBytes(bytes2));
        int i1 = IntegerUtils.fromBytes(bytes1, 0);
        System.out.println("IntegerUtils: " + i1);
        int i2 = ByteUtils.toInt(bytes1, 0);
        System.out.println("ByteUtils: " + i2);
        int i3 = IntegerUtils.fromBytes(bytes2, 0);
        System.out.println("IntegerUtils: " + i3);
        int i4 = ByteUtils.toInt(bytes2, 0);
        System.out.println("ByteUtils: " + i4);
        System.out.println("=================================");
    }
}