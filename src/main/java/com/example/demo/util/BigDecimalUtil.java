package com.example.demo.util;

import com.example.demo.exception.InvalidBigDecimalArgumentException;
import java.math.BigDecimal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BigDecimalUtil {

    public static boolean isGreater(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            throw new InvalidBigDecimalArgumentException("BigDecimal values must not be null");
        }
        return a.compareTo(b) > 0;
    }

    public static boolean isGreaterOrEqual(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            throw new InvalidBigDecimalArgumentException("BigDecimal values must not be null");
        }
        return a.compareTo(b) >= 0;
    }

    public static boolean isLess(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            throw new InvalidBigDecimalArgumentException("BigDecimal values must not be null");
        }
        return a.compareTo(b) < 0;
    }

    public static boolean isLessOrEqual(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            throw new InvalidBigDecimalArgumentException("BigDecimal values must not be null");
        }
        return a.compareTo(b) <= 0;
    }
}
