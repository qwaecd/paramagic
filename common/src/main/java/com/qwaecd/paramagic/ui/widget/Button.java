package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.*;

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

    /**
     * 处理一次 UI 事件, 如果被该节点消耗了, 应将 context 设置为已消耗
     *
     * @param context 本次事件的 context 实例
     */
    @Override
    public void processEvent(UIEventContext context) {
        if (context.mouseEvent == null) {
            return;
        }

        if (context.mouseEvent.isClickOrDouble() && !this.pressed) {
            System.out.println("Button clicked at " + context.mouseEvent.mouseX + ", " + context.mouseEvent.mouseY);
            context.getManager().captureNode(this);
            this.pressed = true;
            context.setConsumed(true);
            return;
        }

        if (context.mouseEvent.isRelease()) {
            if (this.pressed) {
                this.pressed = false;
                context.getManager().releaseCapture();
                context.setConsumed(true);
            }
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
