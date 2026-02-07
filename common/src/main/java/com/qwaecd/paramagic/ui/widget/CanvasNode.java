package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.SizeMode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;

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
    protected void render(@Nonnull UIRenderContext context) {
        super.render(context);
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY, MouseStateMachine mouseState) {
        // 实际上，画布不会随鼠标移动，画布只是一个静态的背景板，真正移动的是内部的可视化图
        this.panX += (float) mouseState.deltaX();
        this.panY += (float) mouseState.deltaY();
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
