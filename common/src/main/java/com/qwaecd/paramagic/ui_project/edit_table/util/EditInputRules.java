package com.qwaecd.paramagic.ui_project.edit_table.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;

public final class EditInputRules {
    private static final BigInteger INT_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger INT_MIN = BigInteger.valueOf(Integer.MIN_VALUE);

    private EditInputRules() {
    }

    @Nonnull
    public static String validateString(@Nullable String text) {
        return text == null ? "" : text;
    }

    @Nonnull
    public static String normalizeFloatText(@Nullable String text) {
        String normalized = text == null ? "" : text.trim();
        if (normalized.length() <= 1) {
            return normalized;
        }
        char suffix = normalized.charAt(normalized.length() - 1);
        if (suffix == 'f' || suffix == 'F' || suffix == 'd' || suffix == 'D') {
            return normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public static float parseFiniteFloat(@Nullable String text) {
        String normalized = normalizeFloatText(text);
        if (normalized.isEmpty() || normalized.equals("+") || normalized.equals("-")
                || normalized.equals(".") || normalized.equals("+.") || normalized.equals("-.")) {
            throw new NumberFormatException("Not a finite float: " + text);
        }

        float value = Float.parseFloat(normalized);
        if (!Float.isFinite(value)) {
            throw new NumberFormatException("Not a finite float: " + text);
        }
        return value;
    }

    public static int parseClampedInt(@Nullable String text) {
        String normalized = text == null ? "" : text.trim();
        if (normalized.isEmpty()) {
            throw new NumberFormatException("Not an int: " + text);
        }

        BigInteger value = new BigInteger(normalized);
        if (value.compareTo(INT_MAX) > 0) {
            return Integer.MAX_VALUE;
        }
        if (value.compareTo(INT_MIN) < 0) {
            return Integer.MIN_VALUE;
        }
        return value.intValue();
    }

    public static int clampMinInt(int value, int minValue) {
        return Math.max(value, minValue);
    }
}
