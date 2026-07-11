package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.SizeMode;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.event.impl.WheelEvent;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;

import javax.annotation.Nonnull;

public class CanvasNode extends MouseCaptureNode {
    // 画布平移量
    protected float panX = 0.0f;
    protected float panY = 0.0f;
    // 画布缩放
    protected float zoom = 1.0f;


    public CanvasNode() {
        this.sizeMode = SizeMode.FILL;
        this.clipMod = ClipMod.RECT;

        this.addListener(AllUIEvents.MOUSE_CLICK, EventPhase.BUBBLING, this::onMouseClick);
        this.addListener(AllUIEvents.MOUSE_DOUBLE_CLICK, EventPhase.BUBBLING, this::onDoubleClick);
        this.addListener(AllUIEvents.MOUSE_RELEASE, EventPhase.BUBBLING, this::onMouseRelease);
        this.addListener(AllUIEvents.WHEEL, EventPhase.BUBBLING, this::onMouseScroll);
    }

    @Override
    public void onMouseClick(UIEventContext<MouseClick> context) {
        if (context.isConsumed()) {
            return;
        }
        super.onMouseClick(context);
    }

    @Override
    public void onDoubleClick(UIEventContext<DoubleClick> context) {
        if (context.isConsumed()) {
            return;
        }
        super.onDoubleClick(context);
    }

    @Override
    public void onMouseRelease(UIEventContext<MouseRelease> context) {
        if (context.isConsumed()) {
            return;
        }
        super.onMouseRelease(context);
    }

    @Override
    public void onMouseScroll(UIEventContext<WheelEvent> context) {
        if (context.isConsumed()) {
            return;
        }
        if (this.ignoreTransform) {
            return;
        }
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
    protected void arrangeChildren() {
        float ox = this.finalRect.x + this.panX;
        float oy = this.finalRect.y + this.panY;

        for (UINode child : this.children) {
            child.arrange(ox, oy, this.finalRect.w, this.finalRect.h);
        }

        if (this.zoom != 1.0f) {
            for (UINode child : this.children) {
                this.applyZoom(child, ox, oy);
            }
        }
    }

    /**
     * 递归地将缩放应用到节点及其所有子节点的最终几何与表现几何上。
     * 缩放后的 finalRect 同时驱动渲染和命中测试。
     */
    private void applyZoom(UINode node, float originX, float originY) {
        node.finalRect.x = originX + (node.finalRect.x - originX) * this.zoom;
        node.finalRect.y = originY + (node.finalRect.y - originY) * this.zoom;
        node.finalRect.w *= this.zoom;
        node.finalRect.h *= this.zoom;
        node.presentationRect.x = originX + (node.presentationRect.x - originX) * this.zoom;
        node.presentationRect.y = originY + (node.presentationRect.y - originY) * this.zoom;
        node.presentationRect.w *= this.zoom;
        node.presentationRect.h *= this.zoom;
        for (UINode child : node.getChildren()) {
            this.applyZoom(child, originX, originY);
        }
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY, MouseStateMachine mouseState) {
        if (this.ignoreTransform) {
            return;
        }
        this.panX += (float) mouseState.deltaX();
        this.panY += (float) mouseState.deltaY();
        this.requestLayout();
    }

    /**
     * 以鼠标为中心进行缩放操作, 缩放前后鼠标位置指向同一个画布点.
     * @param mouseX 当前的鼠标X位置
     * @param mouseY 当前的鼠标Y位置
     * @param newZoom 新的缩放比例, 不可以是 负数 或 0.
     */
    protected void onZoomChanged(float mouseX, float mouseY, float newZoom) {
        if (this.ignoreTransform) {
            return;
        }
        float canvasX = (mouseX - this.finalRect.x - this.panX) / this.zoom;
        float canvasY = (mouseY - this.finalRect.y - this.panY) / this.zoom;
        this.zoom = newZoom;
        this.panX = mouseX - this.finalRect.x - canvasX * this.zoom;
        this.panY = mouseY - this.finalRect.y - canvasY * this.zoom;
        this.requestLayout();
    }

    public float screenToCanvasX(float screenX) {
        return (screenX - this.finalRect.x - this.panX) / this.zoom;
    }

    public float screenToCanvasY(float screenY) {
        return (screenY - this.finalRect.y - this.panY) / this.zoom;
    }

    public float canvasToScreenX(float canvasX) {
        return canvasX * this.zoom + this.finalRect.x + this.panX;
    }

    public float canvasToScreenY(float canvasY) {
        return canvasY * this.zoom + this.finalRect.y + this.panY;
    }
}
