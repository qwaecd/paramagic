package com.qwaecd.paramagic.ui.core;

import lombok.Getter;

/**
 * 父节点在 measure 阶段传给子节点的可用尺寸约束。
 */
@Getter
public final class LayoutConstraints {
    private final float minWidth;
    private final float minHeight;
    private final float maxWidth;
    private final float maxHeight;

    private LayoutConstraints(float minWidth, float minHeight, float maxWidth, float maxHeight) {
        this.minWidth = Math.max(0.0f, minWidth);
        this.minHeight = Math.max(0.0f, minHeight);
        this.maxWidth = Math.max(this.minWidth, maxWidth);
        this.maxHeight = Math.max(this.minHeight, maxHeight);
    }

    public static LayoutConstraints loose(float maxWidth, float maxHeight) {
        return new LayoutConstraints(0.0f, 0.0f, maxWidth, maxHeight);
    }

    public static LayoutConstraints fixed(float width, float height) {
        return new LayoutConstraints(width, height, width, height);
    }

    public static LayoutConstraints bounded(float minWidth, float minHeight, float maxWidth, float maxHeight) {
        return new LayoutConstraints(minWidth, minHeight, maxWidth, maxHeight);
    }

    public float constrainWidth(float width) {
        return Math.max(this.minWidth, Math.min(this.maxWidth, width));
    }

    public float constrainHeight(float height) {
        return Math.max(this.minHeight, Math.min(this.maxHeight, height));
    }

    public MeasureResult constrain(MeasureResult result) {
        return MeasureResult.of(this.constrainWidth(result.getWidth()), this.constrainHeight(result.getHeight()));
    }
}
