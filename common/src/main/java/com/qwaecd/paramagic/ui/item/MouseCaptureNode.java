package com.qwaecd.paramagic.ui.item;

import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.UIEventContext;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;

public class MouseCaptureNode extends UINode {
    protected boolean captured = false;
    // 按下时鼠标相对于节点左上角的偏移
    protected float grabOffsetX = 0.0f;
    protected float grabOffsetY = 0.0f;

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        if (this.captured) {
            return;
        }

        MouseClick event = context.event;
        context.getManager().captureNode(this);
        this.captured = true;
        this.grabOffsetX = (float) event.mouseX - this.worldRect.x;
        this.grabOffsetY = (float) event.mouseY - this.worldRect.y;
        context.consume();
    }

    @Override
    protected void onMouseRelease(UIEventContext<MouseRelease> context) {
        if (this.captured) {
            context.getManager().releaseCapture();
            this.captured = false;
            context.consume();
        }
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        // 计算鼠标在父节点坐标系下的位置
        UINode parent = this.getParent();
        if (parent == null) {
            this.localRect.x = (float) mouseX - this.grabOffsetX;
            this.localRect.y = (float) mouseY - this.grabOffsetY;
            this.layout(
                    0.0f, 0.0f,
                    this.localRect.w, this.localRect.h
            );
        } else {
            this.localRect.x = (float) mouseX - parent.getWorldRect().x - this.grabOffsetX;
            this.localRect.y = (float) mouseY - parent.getWorldRect().y - this.grabOffsetY;

            this.layout(
                    parent.getWorldRect().x, parent.getWorldRect().y,
                    parent.getWorldRect().w, parent.getWorldRect().h
            );
        }
    }
}
