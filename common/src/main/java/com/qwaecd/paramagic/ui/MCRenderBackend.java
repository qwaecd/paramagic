package com.qwaecd.paramagic.ui;

import com.qwaecd.paramagic.ui.core.Rect;
import com.qwaecd.paramagic.ui.core.UIRenderBackend;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class MCRenderBackend implements UIRenderBackend {
    private final GuiGraphics guiGraphics;
    private final Font font;

    public MCRenderBackend(GuiGraphics guiGraphics, Font font) {
        this.guiGraphics = guiGraphics;
        this.font = font;
    }

    @Override
    public void pushClipRect(Rect rect) {
        this.guiGraphics.enableScissor((int) rect.x, (int) rect.y, (int) rect.w, (int) rect.h);
    }

    @Override
    public void popClipRect() {
        this.guiGraphics.disableScissor();
    }

    @Override
    public void drawQuad(Rect rect, UIColor uiColor) {
        this.guiGraphics.fill((int) rect.x, (int) rect.y, (int) (rect.x + rect.w), (int) (rect.y + rect.h), uiColor.color);
    }

    @Override
    public int drawText(Component text, int x, int y, UIColor color, boolean dropShadow) {
        return this.guiGraphics.drawString(this.font, text, x, y, color.color, dropShadow);
    }

    @Override
    public void drawCenteredText(Component text, float centerX, float y, UIColor color) {
        this.guiGraphics.drawCenteredString(this.font, text, (int) centerX, (int) y, color.color);
    }

    @Override
    public int getTextWidth(String text) {
        return this.font.width(text);
    }

    @Override
    public int getTextWidth(Component text) {
        return this.font.width(text);
    }

    @Override
    public int getLineHeight() {
        return this.font.lineHeight;
    }

    @Override
    public void renderOutline(Rect rect, UIColor color) {
        this.guiGraphics.renderOutline((int) rect.x, (int) rect.y, (int) rect.w, (int) rect.h, color.color);
    }
}
