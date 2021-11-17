package lsieun.utils.number;

import java.util.Arrays;

import org.junit.Test;

public class ByteUtilsTest {

    @Test
    public void testMerge() {
        byte[] byte1 = new byte[1];
        byte1[0] = 'a';

        byte[] byte2 = new byte[2];
        byte2[0] = 'b';
        byte2[1] = 'c';

        byte[] byte3 = new byte[3];
        byte3[0] = 'd';
        byte3[1] = 'e';
        byte3[2] = 'f';

        byte[] bytes = ByteUtils.merge(byte1, byte2, byte3);
        System.out.println(Arrays.toString(bytes));
    }

    @Test
    public void testShort() {
        testShort((short) 0);
        testShort((short) 1);
        testShort((short) -1);
        testShort(Short.MAX_VALUE);
        testShort(Short.MIN_VALUE);
    }

    private void testShort(short oldValue) {
        System.out.println("Old Result: " + oldValue);
        byte[] bytes = ByteUtils.fromShort(oldValue);
        System.out.println("HexCode: " + HexUtils.fromBytes(bytes));
        long newValue = ByteUtils.toShort(bytes);
        System.out.println("New Result: " + newValue);
        System.out.println("=======================");
    }

    @Test
    public void testInt() {
        testInt(0);
        testInt(1);
        testInt(-1);
        testInt(Integer.MAX_VALUE);
        testInt(Integer.MIN_VALUE);
    }


    private void testInt(int oldValue) {
        System.out.println("Old Result: " + oldValue);
        byte[] bytes = ByteUtils.fromInt(oldValue);
        System.out.println("HexCode: " + HexUtils.fromBytes(bytes));
        long newValue = ByteUtils.toInt(bytes);
        System.out.println("New Result: " + newValue);
        System.out.println("=======================");
    }

    @Test
    public void testLong() {
        testLong(0);
        testLong(1);
        testLong(-1);
        testLong(Long.MAX_VALUE);
        testLong(Long.MIN_VALUE);
    }

    private void testLong(long oldValue) {
        System.out.println("Old Result: " + oldValue);
        byte[] bytes = ByteUtils.fromLong(oldValue);
        System.out.println("HexCode: " + HexUtils.fromBytes(bytes));
        long newValue = ByteUtils.toLong(bytes);
        System.out.println("New Result: " + newValue);
        System.out.println("=======================");
    }

    @Test
    public void testFloat() {
        testFloat(0);
        testFloat(1);
        testFloat(-1);
        testFloat(3.14F);
        testFloat(Float.MAX_VALUE);
        testFloat(Float.MIN_VALUE);
    }

    private void testFloat(float oldValue) {
        System.out.println("Old Result: " + oldValue);
        byte[] bytes = ByteUtils.fromFloat(oldValue);
        System.out.println("HexCode: " + HexUtils.fromBytes(bytes));
        float newValue = ByteUtils.toFloat(bytes);
        System.out.println("New Result: " + newValue);
        System.out.println("=======================");
    }

    @Test
    public void testDouble() {
        testDouble(0);
        testDouble(1);
        testDouble(-1);
        testDouble(3.14);
        testDouble(Math.PI);
        testDouble(Math.E);
        testDouble(Double.MAX_VALUE);
        testDouble(Double.MIN_VALUE);
    }

    private void testDouble(double oldValue) {
        System.out.println("Old Result: " + oldValue);
        byte[] bytes = ByteUtils.fromDouble(oldValue);
        System.out.println("HexCode: " + HexUtils.fromBytes(bytes));
        double newValue = ByteUtils.toDouble(bytes);
        System.out.println("New Result: " + newValue);
        System.out.println("=======================");
    }

    @Test
    public void testUtf8() {
        String str = "HelloWorld你好世界";
        System.out.println("Origin Str: " + str);
        byte[] bytes = ByteUtils.fromUtf8(str);
        System.out.println("HexCode: " + HexUtils.fromBytes(bytes));
        String newStr = ByteUtils.toUtf8(bytes);
        System.out.println("New Str: " + newStr);
    }
}