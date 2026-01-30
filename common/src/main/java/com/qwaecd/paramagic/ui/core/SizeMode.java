package com.qwaecd.paramagic.ui.core;

public enum SizeMode {
    /**
     * 固定尺寸：使用localRect的w/h
     */
    FIXED,
    /**
     * 完全填充：w和h都等于父容器
     */
    FILL,
    /**
     * 仅宽度填充：w等于父容器，h使用localRect.h
     */
    FILL_WIDTH,
    /**
     * 仅高度填充：h等于父容器，w使用localRect.w
     */
    FILL_HEIGHT;
}
