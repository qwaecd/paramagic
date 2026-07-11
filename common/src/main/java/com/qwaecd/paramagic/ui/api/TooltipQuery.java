package com.qwaecd.paramagic.ui.api;

import javax.annotation.Nonnull;

/**
 * 一次 tooltip 查询的只读输入。坐标为 GUI 屏幕空间坐标。
 */
public record TooltipQuery(float mouseX, float mouseY, @Nonnull Trigger trigger) {
    public enum Trigger {
        HOVER,
        CAPTURED
    }
}
