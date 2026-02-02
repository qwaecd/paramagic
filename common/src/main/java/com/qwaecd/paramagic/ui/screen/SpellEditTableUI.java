package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import com.qwaecd.paramagic.ui.io.mouse.MouseEvent;
import org.jetbrains.annotations.NotNull;

public class SpellEditTableUI extends UINode {
    public SpellEditTableUI() {
        super();
        {
            UINode uiNode = new CaptureAbleNode();
            uiNode.setShowDebugOutLine(true);
            uiNode.localRect.set(50, 30, 100, 80);
            uiNode.setBackgroundColor(UIColor.BLUE);
            {
                UINode subNode = new CaptureAbleNode();
                subNode.setShowDebugOutLine(true);
                subNode.localRect.set(150, 80, 90, 80);
                subNode.setBackgroundColor(UIColor.WHITE);
                uiNode.addChild(subNode);
            }
            this.addChild(uiNode);
        }
        {
            UINode uiNode = new CaptureAbleNode();
            uiNode.setShowDebugOutLine(true);
            uiNode.localRect.set(10, 20, 80, 60);
            uiNode.setBackgroundColor(UIColor.BLACK);
            this.addChild(uiNode);
        }
        {
            UINode uiNode = new CaptureAbleNode();
            uiNode.setShowDebugOutLine(true);
            uiNode.localRect.set(70, 30, 200, 60);
            uiNode.setBackgroundColor(UIColor.GREEN);
            this.addChild(uiNode);
        }
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        this.localRect.set(50.0f, 30.0f, 200.0f, 150.0f);
        this.showDebugOutLine = true;
        super.layout(this.localRect.x, this.localRect.y, this.localRect.w, this.localRect.h);
    }

    @Override
    public void render(@NotNull UIRenderContext context) {
        context.drawQuad(this.worldRect, new UIColor(UIColor.fromRGBA(255, 0, 0, 100)));
        if (this.showDebugOutLine) {
            context.renderOutline(this.worldRect, UIColor.RED);
        }
    }

    private static class CaptureAbleNode extends UINode {
        private boolean captured = false;
        // 按下时鼠标相对于节点左上角的偏移
        private float grabOffsetX = 0;
        private float grabOffsetY = 0;
        @Override
        public void processEvent(UIEventContext context) {
            MouseEvent mouseEvent = context.mouseEvent;
            if (mouseEvent == null) {
                return;
            }

            if (mouseEvent.isClickOrDouble() && !this.captured) {
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
}
