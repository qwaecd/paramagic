package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.UIColor;

import javax.annotation.Nonnull;

public class UIButton extends UINode {
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
        this.whenClickOrDouble(context.event.mouseX, context.event.mouseY, context.getManager(), false);
        context.consumeAndStopPropagation();
    }

    @Override
    protected void onDoubleClick(UIEventContext<DoubleClick> context) {
        this.whenClickOrDouble(context.event.mouseX, context.event.mouseY, context.getManager(), true);
        context.consumeAndStopPropagation();
    }

    protected void whenClickOrDouble(double mouseX, double mouseY, UIManager manager, boolean isDouble) {
        if (this.pressed) {
            return;
        }
        manager.captureNode(this);
        this.pressed = true;
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
            context.drawQuad(this.worldRect, UIColor.fromRGBA(200, 200, 200, 100));
        } else {
            context.drawQuad(this.worldRect, UIColor.fromRGBA(127, 127, 127, 200));
        }
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        super.layout(parentX, parentY, parentW, parentH);
    }
}
