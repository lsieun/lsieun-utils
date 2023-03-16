package lsieun.utils.number;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
    private static final int NUMBER_COMMON_PRECISION = 3;

    public static double normalize(double val) {
        BigDecimal normalizedValue = normalize(BigDecimal.valueOf(val));
        return normalizedValue.doubleValue();
    }

    public static double normalize(double val, int newScale) {
        BigDecimal normalizedValue = normalize(BigDecimal.valueOf(val), newScale);
        return normalizedValue.doubleValue();
    }

    public static double normalize(double val, int newScale, RoundingMode roundingMode) {
        BigDecimal normalizedValue = normalize(BigDecimal.valueOf(val), newScale, roundingMode);
        return normalizedValue.doubleValue();
    }

    public static BigDecimal normalize(BigDecimal val) {
        return normalize(val, NUMBER_COMMON_PRECISION);
    }

    public static BigDecimal normalize(BigDecimal val, int newScale) {
        return normalize(val, newScale, RoundingMode.HALF_UP);
    }

    public static BigDecimal normalize(BigDecimal val, int newScale, RoundingMode roundingMode) {
        return val.setScale(newScale, roundingMode);
    }
}
