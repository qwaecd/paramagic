package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.util.NineSliceSprite;
import com.qwaecd.paramagic.ui_project.edit_table.EditTableSprite;

public class ContextMenu extends UINode {
    protected static final NineSliceSprite sprite =
            NineSliceSprite.builder(ModRL.inModSpace("textures/gui/edit_table.png"), EditTableSprite.TEX_W, EditTableSprite.TEX_H)
                    .slice(0, 80,  224, 16, 16)
                    .slice(1, 96,  224, 16, 16)
                    .slice(2, 112, 224, 16, 16)
                    .slice(3, 80,  240, 16, 16)
                    .slice(4, 96,  240, 16, 16)
                    .slice(5, 112, 240, 16, 16)
                    .slice(6, 80,  256, 16, 16)
                    .slice(7, 96,  256, 16, 16)
                    .slice(8, 112, 256, 16, 16)
                    .build();

    public ContextMenu() {
    }

    public void cancel() {
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        final float screenW = this.getWindowWidth() / this.getGuiScale();
        final float screenH = this.getWindowHeight() / this.getGuiScale();
        if (this.localRect.x + this.localRect.w > screenW) {
            this.localRect.x = screenW - this.localRect.w;
        }
        if (this.localRect.y + this.localRect.h > screenH) {
            this.localRect.y = screenH - this.localRect.h;
        }
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        context.consumeAndStopPropagation();
    }

    @Override
    protected void renderBackGround(UIRenderContext context) {
        context.renderNineSliceSprite(
                sprite,
                (int) this.worldRect.x,
                (int) this.worldRect.y,
                (int) this.worldRect.w,
                (int) this.worldRect.h
        );
    }
}
