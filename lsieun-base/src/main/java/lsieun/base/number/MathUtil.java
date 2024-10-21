package lsieun.base.number;

public final class MathUtil {
    private MathUtil() {
    }

    public static int nonNegativeAbs(int a) {
        return a >= 0 ? a : (a == Integer.MIN_VALUE ? Integer.MAX_VALUE : -a);
    }

    public static int clamp(int value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(min + ">" + max);
        }
        else {
            return Math.min(max, Math.max(value, min));
        }
    }

    public static long clamp(long value, long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException(min + ">" + max);
        }
        else {
            return Math.min(max, Math.max(value, min));
        }
    }

    public static double clamp(double value, double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException(min + ">" + max);
        }
        else {
            return Math.min(max, Math.max(value, min));
        }
    }

    public static float clamp(float value, float min, float max) {
        if (min > max) {
            throw new IllegalArgumentException(min + ">" + max);
        }
        else {
            return Math.min(max, Math.max(value, min));
        }
    }

    public static boolean equals(float a, float b, float epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    public static boolean equals(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    public static int compare(float a, float b, float epsilon) {
        return Math.abs(a - b) < epsilon ? 0 : Float.compare(a, b);
    }

    public static int compare(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon ? 0 : Double.compare(a, b);
    }

    public static boolean between(float min, float med, float max, float epsilon) {
        return compare(min, med, epsilon) < 0 && compare(med, max, epsilon) < 0;
    }

    public static boolean between(double min, double med, double max, double epsilon) {
        return compare(min, med, epsilon) < 0 && compare(med, max, epsilon) < 0;
    }
}