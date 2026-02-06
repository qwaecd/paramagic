package com.qwaecd.paramagic.ui;

import com.qwaecd.paramagic.ui.core.LayoutParams;
import com.qwaecd.paramagic.ui.core.Rect;
import com.qwaecd.paramagic.ui.core.SizeMode;

import javax.annotation.Nonnull;

public final class UILayout {
    private UILayout() {
    }

    /**
     * 进行一次元素布局.
     * @param localRect 本元素的局部矩形
     * @param worldRect 本元素的屏幕矩形
     * @param layoutParams 布局参数
     * @param parentX 父节点的屏幕X坐标
     * @param parentY 父节点的屏幕Y坐标
     * @param parentW 父节点的矩形宽度
     * @param parentH 父节点矩形的高度
     */
    public static void layout(
            @Nonnull Rect localRect,
            @Nonnull Rect worldRect,
            @Nonnull LayoutParams layoutParams,
            @Nonnull SizeMode sizeMode,
            float parentX,
            float parentY,
            float parentW,
            float parentH
    ) {
        if (layoutParams.isEnabled()) {
            // 将自身锚点强制与父锚点相对齐
            // 会直接无视初始设定的 localRect 的 xy 并重新设置
            localRect.setXY(
                    parentW * layoutParams.getAnchorX() - localRect.w * layoutParams.getPivotX(),
                    parentH * layoutParams.getAnchorY() - localRect.h * layoutParams.getPivotY()
            );
        }

        float width = computeWidth(sizeMode, localRect, parentW);
        float height = computeHeight(sizeMode, localRect, parentH);

        worldRect.set(
                parentX + localRect.x,
                parentY + localRect.y,
                width,
                height
        );
    }

    private static float computeWidth(SizeMode sizeMode, Rect localRect, float baseW) {
        return switch (sizeMode) {
            case FIXED, FILL_HEIGHT -> localRect.w;
            case FILL, FILL_WIDTH -> baseW;
        };
    }

    private static float computeHeight(SizeMode sizeMode, Rect localRect, float baseH) {
        return switch (sizeMode) {
            case FIXED, FILL_WIDTH -> localRect.h;
            case FILL, FILL_HEIGHT -> baseH;
        };
    }
}
