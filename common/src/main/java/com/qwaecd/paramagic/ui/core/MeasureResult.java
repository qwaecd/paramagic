package com.qwaecd.paramagic.ui.core;

import lombok.Getter;

/**
 * 节点在 measure 阶段产出的自然尺寸。
 */
@Getter
public final class MeasureResult {
    public static final MeasureResult ZERO = new MeasureResult(0.0f, 0.0f);

    private final float width;
    private final float height;

    private MeasureResult(float width, float height) {
        this.width = Math.max(0.0f, width);
        this.height = Math.max(0.0f, height);
    }

    public static MeasureResult of(float width, float height) {
        if (width == 0.0f && height == 0.0f) {
            return ZERO;
        }
        return new MeasureResult(width, height);
    }
}
