package com.antonchaynikov.triptracker.utils;

import java.util.Locale;

public final class StringUtils {

    private StringUtils() {}

    public static String numToFormattedString(Number num) {
        return String.format(Locale.getDefault(), "%.2f", num.doubleValue());
    }

    public static String numToFormattedString(Number num, boolean hideZeroDecimals) {
        if (hideZeroDecimals && !hasDecimals(num)) {
            return String.format(Locale.getDefault(), "%.0f", num.doubleValue());
        }
        return String.format(Locale.getDefault(), "%.2f", num.doubleValue());
    }

    public static String timeFieldToString(long timeFieldValue) {
        if (timeFieldValue > 60) {
            throw new IllegalArgumentException("Should be less than 60: " + timeFieldValue);
        }
        return String.format(Locale.getDefault(), "%02d", timeFieldValue);
    }

    private static boolean hasDecimals(Number num) {
        return num.doubleValue() - Math.floor(num.doubleValue()) >= 0.01;
    }

}
