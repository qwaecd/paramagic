package com.qwaecd.paramagic.ui.api;

import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.Sprite;
import com.qwaecd.paramagic.ui.util.UIColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface UIRenderBackend {
    void pushClipRect(Rect rect);
    void popClipRect();

    void vLine(int x, int minY, int maxY, int color);
    void hLine(int minX, int maxX, int y, int color);

    default void drawQuad(Rect rect, UIColor color) {
        drawQuad(rect, color.color);
    }

    /**
     * Draws a component's visual order text at the specified coordinates using the given font, text component, color, and drop shadow.
     * <p>
     * @return the width of the drawn string.
     *
     * @param text       the text component to draw.
     * @param x          the x-coordinate of the string.
     * @param y          the y-coordinate of the string.
     * @param color      the color of the string.
     * @param dropShadow whether to apply a drop shadow to the string.
     */
    default int drawText(Component text, int x, int y, UIColor color, boolean dropShadow) {
        return drawText(text, x, y, color.color, dropShadow);
    }
    default int drawText(Component text, int x, int y, UIColor color) {
        return drawText(text, x, y, color.color, false);
    }

    default void drawCenteredText(Component text, float centerX, float y, UIColor color) {
        drawCenteredText(text, centerX, y, color.color);
    }

    int getTextWidth(String text);
    int getTextWidth(Component text);
    int getLineHeight();

    default void renderOutline(Rect rect, UIColor color) {
        this.renderOutline((int) rect.x, (int) rect.y, (int) rect.w, (int) rect.h, color.color);
    }
    default void renderOutline(int x, int y, int w, int h, UIColor color) {
        this.renderOutline(x, y, w, h, color.color);
    }

    /**
     * 在指定位置渲染精灵图.
     * @param sprite 精灵图实例.
     * @param x 绘制区域左上角的 x 坐标.
     * @param y 绘制区域左上角的 y 坐标.
     */
    void renderSprite(Sprite sprite, int x, int y);

    void renderItem(ItemStack stack, int x, int y);

    void renderItemDecorations(ItemStack stack, int x, int y, @Nullable String text);

    void drawQuad(Rect rect, int color);

    /**
     * 在指定的区域绘制纯色矩形
     * @param minX 矩形左上角 X
     * @param minY 矩形左上角 Y
     * @param maxX 矩形右下角 X
     * @param maxY 矩形右下角 Y
     * @param color 矩形颜色
     */
    void fill(int minX, int minY, int maxX, int maxY, int color);
    int drawText(Component text, int x, int y, int color, boolean dropShadow);
    default int drawText(Component text, int x, int y, int color) {
        return drawText(text, x, y, color, false);
    }
    void drawCenteredText(Component text, float centerX, float y, int color);
    void renderOutline(int x, int y, int w, int h, int color);
    default void renderOutline(Rect rect, int color) {
        this.renderOutline((int) rect.x, (int) rect.y, (int) rect.w, (int) rect.h, color);
    }
}
