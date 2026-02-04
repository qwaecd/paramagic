package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.*;
import com.qwaecd.paramagic.ui.event.UIEventContext;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;

import javax.annotation.Nonnull;

public class Button extends UINode {
    private static final Sprite debugSprite = new Sprite(
            ModRL.InModSpace("textures/magic/circle_01.png"),
            0, 0, 128, 128, 128, 128
    );

    private boolean pressed = false;

    public Button() {
        super();
    }

    public Button(Rect localRect) {
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

    private void whenClickOrDouble(double mouseX, double mouseY, UIManager manager, boolean isDouble) {
        if (this.pressed) {
            return;
        }
        System.out.println("Button clicked at " + mouseX + ", " + mouseY + (isDouble ? " (double click)" : ""));
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
        if (this.pressed) {
            context.drawQuad(this.worldRect, UIColor.of(127, 127, 127, 100));
        } else {
            context.drawQuad(this.worldRect, UIColor.RED);
        }

        context.renderSprite(debugSprite, this.worldRect.x, this.worldRect.y);
        context.drawText("这是六个文字", this.worldRect.x + 5.0f, this.worldRect.y + 5.0f, UIColor.WHITE);
        super.render(context);
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        super.layout(parentX, parentY, parentW, parentH);
    }
}
