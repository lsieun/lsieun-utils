package lsieun.utils.core.number;

import org.junit.jupiter.api.Test;

import static lsieun.utils.core.number.BitBuddy.*;
import static org.junit.jupiter.api.Assertions.*;

class BitBuddyTest {
    @Test
    void testParseAndFormat() {
        for (int i = 0; i < 256; i++) {
            byte b = (byte) i;
            String binaryStr = format(b);
            byte newB = (byte) parse(binaryStr);
            if (b != newB) {
                assertEquals(b, newB);
            }
        }
    }

    @Test
    void testHasBit() {
        for (int i = 0; i < 256; i++) {
            byte b = (byte) i;
            String binaryStr = format(b);

            for (int j = 0; j < 8; j++) {
                boolean hasBit = BitBuddy.hasBit(b, j);
                boolean isOne = binaryStr.charAt(7 - j) == '1';
                assertEquals(hasBit, isOne);
            }
        }
    }

    @Test
    void testSetBit() {
        for (int i = 0; i < 8; i++) {
            byte b = 0;
            byte newB = setBit(b, i);
            boolean hasBit = hasBit(newB, i);
            assertTrue(hasBit);
        }
    }

    @Test
    void testClearBit() {
        for (int i = 0; i < 8; i++) {
            byte b = (byte) 0xFF;
            byte newB = clearBit(b, i);
            boolean hasBit = hasBit(newB, i);
            assertFalse(hasBit);
        }
    }

    @Test
    void testToggleBit() {
        for (int i = 0; i < 8; i++) {
            byte b = 0;
            byte newB = toggleBit(b, i);
            boolean hasBit = hasBit(newB, i);
            assertTrue(hasBit);

            newB = toggleBit(newB, i);
            hasBit = hasBit(newB, i);
            assertFalse(hasBit);
        }
    }

    @Test
    void testToggleBitArray() {
        for (int i = 0; i < 256; i++) {
            byte b = (byte) i;

            int[] array = {0, 1, 2, 3, 4, 5, 6, 7};

            byte newB = toggleBit(b, array);

            for (int index : array) {
                boolean hasBit1 = hasBit(b, index);
                boolean hasBit2 = hasBit(newB, index);

                assertNotEquals(hasBit1, hasBit2);
            }
        }
    }

    @Test
    void testSwapBit() {
        for (int i = 0; i < 256; i++) {
            byte b = (byte) i;

            for (int j = 0; j < 8; j++) {
                int k = (j + 1) % 8;

                boolean hasBit1 = hasBit(b, j);
                boolean hasBit2 = hasBit(b, k);

                byte newB = swapBit(b, j, k);
                boolean hasBit3 = hasBit(newB, j);
                boolean hasBit4 = hasBit(newB, k);

                assertEquals(hasBit1, hasBit4);
                assertEquals(hasBit2, hasBit3);
            }
        }
    }

    @Test
    void testMove() {
        for (int i = 0; i < 256; i++) {
            byte b = (byte) i;

            int[] array = {0, 2, 4, 6, 7, 5, 3, 1};

            byte newB = move(b, array);

            int[] array2 = {6, 4, 2, 0, 1, 3, 5, 7};

            byte newB2 = move(newB, array2);

            String msg = String.format("b = %d, newB = %d, newB2 = %d",
                    b, newB, newB2);
            System.out.println(msg);


            assertEquals(b, newB2);
        }
    }


    @Test
    void testSwapByIndexArray() {
        int[] array = {0, 7, 1, 6, 2, 5, 3, 4};

        for (int i = 0; i < 256; i++) {
            byte b = (byte) i;

            byte newB = swapByIndexArray(b, array);

            byte newB2 = swapByIndexArray(newB, array);

            byte newB3 = swap(b);

            System.out.println(format(b));
            System.out.println(format(newB2));
            System.out.println(format(newB));
            System.out.println(format(newB3));
            System.out.println("======");

            assertEquals(b, newB2);

            assertEquals(newB, newB3);
        }
    }
}