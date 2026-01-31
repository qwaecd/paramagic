package com.qwaecd.paramagic.ui;

import com.qwaecd.paramagic.ui.core.Rect;
import com.qwaecd.paramagic.ui.core.UIRenderBackend;
import net.minecraft.client.gui.GuiGraphics;

public class MCRenderBackend implements UIRenderBackend {
    private final GuiGraphics guiGraphics;

    public MCRenderBackend(GuiGraphics guiGraphics) {
        this.guiGraphics = guiGraphics;
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
    public void renderOutline(Rect rect, UIColor color) {
        this.guiGraphics.renderOutline((int) rect.x, (int) rect.y, (int) rect.w, (int) rect.h, color.color);
    }
}
