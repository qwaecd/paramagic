package com.qwaecd.paramagic.ui_project.edit_table.util;

import com.qwaecd.paramagic.ui.core.UIManager;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class EditNumericInputSupport {
    public static final int MAX_FLOAT_DECIMAL_PLACES = 4;

    private EditNumericInputSupport() {
    }

    public static int getScrollDirection(double scrollDelta) {
        if (scrollDelta > 0.0) {
            return 1;
        }
        if (scrollDelta < 0.0) {
            return -1;
        }
        return 0;
    }

    public static float getFloatStep() {
        float multiplier = 1.0f;
        if (UIManager.hasCtrlKeyDown()) {
            multiplier *= 10.0f;
        }
        if (UIManager.hasShiftKeyDown()) {
            multiplier *= 0.1f;
        }
        if (UIManager.hasAltKeyDown()) {
            multiplier *= 50.0f;
        }
        return multiplier;
    }

    public static int getIntStep() {
        int multiplier = 1;
        if (UIManager.hasCtrlKeyDown()) {
            multiplier *= 10;
        }
        if (UIManager.hasAltKeyDown()) {
            multiplier *= 50;
        }
        return multiplier;
    }

    @Nonnull
    public static String formatFloat(float value) {
        BigDecimal rounded = new BigDecimal(Float.toString(value))
                .setScale(MAX_FLOAT_DECIMAL_PLACES, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        String text = rounded.toPlainString();
        return text.equals("-0") ? "0" : text;
    }
}
