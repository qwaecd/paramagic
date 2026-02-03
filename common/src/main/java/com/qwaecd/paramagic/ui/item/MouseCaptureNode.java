package com.qwaecd.paramagic.ui.item;

import com.qwaecd.paramagic.ui.core.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.io.mouse.MouseEvent;

public class MouseCaptureNode extends UINode {
    protected boolean captured = false;
    // 按下时鼠标相对于节点左上角的偏移
    protected float grabOffsetX = 0.0f;
    protected float grabOffsetY = 0.0f;

    @Override
    public void processEvent(UIEventContext context) {
        MouseEvent mouseEvent = context.mouseEvent;
        if (mouseEvent == null) {
            return;
        }

        if (this.captureCondition(mouseEvent)) {
            context.getManager().captureNode(this);
            this.captured = true;
            this.grabOffsetX = (float) mouseEvent.mouseX - this.worldRect.x;
            this.grabOffsetY = (float) mouseEvent.mouseY - this.worldRect.y;
        }

        if (mouseEvent.isRelease()) {
            context.getManager().releaseCapture();
            this.captured = false;
        }

        context.consume();
    }

    protected boolean captureCondition(MouseEvent mouseEvent) {
        return mouseEvent.isClickOrDouble() && !this.captured;
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
