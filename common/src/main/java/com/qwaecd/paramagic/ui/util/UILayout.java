package com.qwaecd.paramagic.ui.util;

import com.qwaecd.paramagic.ui.core.SizeMode;

import javax.annotation.Nonnull;

public final class UILayout {
    private UILayout() {
    }

    /**
     * 进行一次元素布局.
     * @param layoutRect 本元素的布局输入矩形
     * @param finalRect 本元素的最终屏幕矩形
     * @param layoutParams 布局参数
     * @param parentX 父节点的屏幕X坐标
     * @param parentY 父节点的屏幕Y坐标
     * @param parentW 父节点的矩形宽度
     * @param parentH 父节点矩形的高度
     */
    public static void layout(
            @Nonnull Rect layoutRect,
            @Nonnull Rect finalRect,
            @Nonnull LayoutParams layoutParams,
            @Nonnull SizeMode sizeMode,
            float parentX,
            float parentY,
            float parentW,
            float parentH
    ) {
        if (layoutParams.isEnabled()) {
            // 将自身锚点强制与父锚点相对齐
            // 会直接无视初始设定的 layoutRect 的 xy 并重新设置
            layoutRect.setXY(
                    parentW * layoutParams.getAnchorX() - layoutRect.w * layoutParams.getPivotX(),
                    parentH * layoutParams.getAnchorY() - layoutRect.h * layoutParams.getPivotY()
            );
        }

        float width = resolveWidth(sizeMode, layoutRect, parentW);
        float height = resolveHeight(sizeMode, layoutRect, parentH);

        finalRect.set(
                parentX + layoutRect.x,
                parentY + layoutRect.y,
                width,
                height
        );
    }

    public static float resolveWidth(SizeMode sizeMode, Rect layoutRect, float baseW) {
        return switch (sizeMode) {
            case FIXED, FILL_HEIGHT -> layoutRect.w;
            case FILL, FILL_WIDTH -> baseW;
        };
    }

    public static float resolveHeight(SizeMode sizeMode, Rect layoutRect, float baseH) {
        return switch (sizeMode) {
            case FIXED, FILL_WIDTH -> layoutRect.h;
            case FILL, FILL_HEIGHT -> baseH;
        };
    }
}
