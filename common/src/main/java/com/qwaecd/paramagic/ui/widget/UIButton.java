package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.util.NineSliceSprite;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui_project.edit_table.EditTableSprite;

import javax.annotation.Nonnull;

public class UIButton extends UINode {
    protected static final NineSliceSprite buttonSprite =
            NineSliceSprite.builder(ModRL.inModSpace("textures/gui/edit_table.png"), EditTableSprite.TEX_W, EditTableSprite.TEX_H)
                    .slice(0, 0,  224, 16, 16)
                    .slice(1, 16, 224, 32, 16)
                    .slice(2, 48, 224, 16, 16)
                    .slice(3, 0,  240, 16, 16)
                    .slice(4, 16, 240, 32, 16)
                    .slice(5, 48, 240, 16, 16)
                    .slice(6, 0,  256, 16, 16)
                    .slice(7, 16, 256, 32, 16)
                    .slice(8, 48, 256, 16, 16)
                    .build();
    protected boolean pressed = false;

    public UIButton() {
        super();
    }

    public UIButton(Rect localRect) {
        super();
        this.localRect.set(localRect);
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        if (this.pressed) {
            return;
        }
        context.manager.captureNode(this);
        this.pressed = true;
        context.consumeAndStopPropagation();
    }

    @Override
    protected void onDoubleClick(UIEventContext<DoubleClick> context) {
        if (this.pressed) {
            return;
        }
        context.manager.captureNode(this);
        this.pressed = true;
        context.consumeAndStopPropagation();
    }

    @Override
    protected void onMouseRelease(UIEventContext<MouseRelease> context) {
        if (this.pressed) {
            this.pressed = false;
            context.getManager().releaseCapture();
            context.consume();
        }
    }

    @Override
    public void render(@Nonnull UIRenderContext context) {
        super.render(context);
        if (this.pressed) {
            context.renderOutline(this.worldRect, UIColor.fromRGBA(255, 255, 255, 200));
        }
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void renderBackGround(UIRenderContext context) {
        context.renderNineSliceSprite(
                buttonSprite,
                (int) this.worldRect.x, (int) this.worldRect.y,
                (int) this.worldRect.w, (int) this.worldRect.h
        );
    }
}
