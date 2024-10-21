package lsieun.base.number;

import jakarta.annotation.Nonnull;

public final class BitUtil {
    public static boolean isSet(byte value, byte mask) {
        assertOneBitMask(mask);
        return (value & mask) == mask;
    }

    public static boolean isSet(int value, int mask) {
        assertOneBitMask(mask);
        return (value & mask) == mask;
    }

    public static boolean isSet(long flags, long mask) {
        assertOneBitMask(mask);
        return (flags & mask) == mask;
    }

    public static byte set(byte value, byte mask, boolean setBit) {
        assertOneBitMask(mask);
        return (byte)(setBit ? value | mask : value & ~mask);
    }


    public static int set(int value, int mask, boolean setBit) {
        assertOneBitMask(mask);
        return setBit ? value | mask : value & ~mask;
    }


    public static long set(long value, long mask, boolean setBit) {
        assertOneBitMask(mask);
        return setBit ? value | mask : value & ~mask;
    }


    public static byte clear(byte value, byte mask) {
        return set(value, mask, false);
    }


    public static int clear(int value, int mask) {
        return set(value, mask, false);
    }


    public static long clear(long value, long mask) {
        return set(value, mask, false);
    }

    private static void assertOneBitMask(byte mask) {
        assertOneBitMask((long)mask & 255L);
    }

    public static void assertOneBitMask(int mask) {
        assert (mask & mask - 1) == 0 : invalidMaskError(mask);

    }

    private static void assertOneBitMask(long mask) {
        assert (mask & mask - 1L) == 0L : invalidMaskError(mask);

    }

    private static @Nonnull String invalidMaskError(long mask) {
        return "Mask must have only one bit set, but got: " + Long.toBinaryString(mask);
    }
}