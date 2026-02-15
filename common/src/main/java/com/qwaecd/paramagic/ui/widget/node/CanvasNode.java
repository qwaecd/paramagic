package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.SizeMode;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.WheelEvent;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui.util.UILayout;

import javax.annotation.Nonnull;

public class CanvasNode extends MouseCaptureNode {
    // 画布平移量
    protected float panX = 0.0f;
    protected float panY = 0.0f;
    // 画布缩放
    protected float zoom = 1.0f;

    public CanvasNode() {
        this.backgroundColor = UIColor.of(1, 1, 1, 200);
        this.sizeMode = SizeMode.FILL;
        this.clipMod = ClipMod.RECT;
    }

    @Override
    protected void onMouseScroll(UIEventContext<WheelEvent> context) {
        WheelEvent event = context.event;
        this.onZoomChanged(
                (float) event.mouseX,
                (float) event.mouseY,
                this.zoom * (float) Math.pow(1.1, event.scrollDelta)
        );
        context.consume();
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        super.render(context);
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        UILayout.layout(this.localRect, this.worldRect, this.layoutParams, this.sizeMode, parentX, parentY, parentW, parentH);

        float ox = this.worldRect.x + this.panX;
        float oy = this.worldRect.y + this.panY;

        for (UINode child : this.children) {
            child.layout(ox, oy, this.worldRect.w, this.worldRect.h);
        }

        if (this.zoom != 1.0f) {
            for (UINode child : this.children) {
                applyZoom(child, ox, oy);
            }
        }
    }

    /**
     * 递归地将缩放应用到节点及其所有子节点的 worldRect 上.
     */
    private void applyZoom(UINode node, float originX, float originY) {
        node.worldRect.x = originX + (node.worldRect.x - originX) * this.zoom;
        node.worldRect.y = originY + (node.worldRect.y - originY) * this.zoom;
        node.worldRect.w *= this.zoom;
        node.worldRect.h *= this.zoom;
        for (UINode child : node.getChildren()) {
            applyZoom(child, originX, originY);
        }
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY, MouseStateMachine mouseState) {
        this.panX += (float) mouseState.deltaX();
        this.panY += (float) mouseState.deltaY();
        relayout();
    }

    /**
     * 以鼠标为中心进行缩放操作, 缩放前后鼠标位置指向同一个画布点.
     * @param mouseX 当前的鼠标X位置
     * @param mouseY 当前的鼠标Y位置
     * @param newZoom 新的缩放比例, 不可以是 负数 或 0.
     */
    protected void onZoomChanged(float mouseX, float mouseY, float newZoom) {
        float canvasX = (mouseX - this.worldRect.x - this.panX) / this.zoom;
        float canvasY = (mouseY - this.worldRect.y - this.panY) / this.zoom;
        this.zoom = newZoom;
        this.panX = mouseX - this.worldRect.x - canvasX * this.zoom;
        this.panY = mouseY - this.worldRect.y - canvasY * this.zoom;
        relayout();
    }

    private void relayout() {
        UINode parent = this.getParent();
        if (parent != null) {
            this.layout(parent.getWorldRect().x, parent.getWorldRect().y,
                    parent.getWorldRect().w, parent.getWorldRect().h);
        } else {
            this.layout(0, 0, this.localRect.w, this.localRect.h);
        }
    }

    public float screenToCanvasX(float screenX) {
        return (screenX - this.worldRect.x - this.panX) / this.zoom;
    }

    public float screenToCanvasY(float screenY) {
        return (screenY - this.worldRect.y - this.panY) / this.zoom;
    }

    public float canvasToScreenX(float canvasX) {
        return canvasX * this.zoom + this.worldRect.x + this.panX;
    }

    public float canvasToScreenY(float canvasY) {
        return canvasY * this.zoom + this.worldRect.y + this.panY;
    }
}
