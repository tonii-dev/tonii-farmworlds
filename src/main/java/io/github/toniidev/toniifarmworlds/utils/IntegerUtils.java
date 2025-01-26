package io.github.toniidev.toniifarmworlds.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IntegerUtils {
    /**
     * Rounds the specified number to the specified decimal places
     *
     * @param number        The double to round
     * @param decimalPlaces The decimal places to round the number to
     * @return The specified double rounded to the specified decimal place
     */
    public static double round(Double number, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);  // Round to the specified number of decimals
        return bd.doubleValue();
    }

    public static double roundCompletely(Double number){
        return round(number, 0);
    }
}
