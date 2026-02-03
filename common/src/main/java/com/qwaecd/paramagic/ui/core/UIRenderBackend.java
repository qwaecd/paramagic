package com.qwaecd.paramagic.ui.core;

import com.qwaecd.paramagic.ui.UIColor;
import net.minecraft.network.chat.Component;

public interface UIRenderBackend {
    void pushClipRect(Rect rect);
    void popClipRect();
    void drawQuad(Rect rect, UIColor color);

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
    int drawText(Component text, int x, int y, UIColor color, boolean dropShadow);
    default int drawText(Component text, int x, int y, UIColor color) {
        return drawText(text, x, y, color, false);
    }

    void drawCenteredText(Component text, float centerX, float y, UIColor color);

    int getTextWidth(String text);
    int getTextWidth(Component text);
    int getLineHeight();

    void renderOutline(Rect rect, UIColor color);
}
