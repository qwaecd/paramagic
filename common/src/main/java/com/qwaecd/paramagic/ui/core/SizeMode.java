package com.qwaecd.paramagic.ui.core;

public enum SizeMode {
    /**
     * 固定尺寸：使用 layoutRect 的 w/h。
     */
    FIXED,
    /**
     * 完全填充：w和h都等于父容器
     */
    FILL,
    /**
     * 仅宽度填充：w 等于父容器，h 使用 layoutRect.h。
     */
    FILL_WIDTH,
    /**
     * 仅高度填充：h 等于父容器，w 使用 layoutRect.w。
     */
    FILL_HEIGHT;
}
