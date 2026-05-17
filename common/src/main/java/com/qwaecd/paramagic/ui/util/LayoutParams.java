package com.qwaecd.paramagic.ui.util;

import lombok.Getter;

import javax.annotation.Nullable;

/**
 * 使用给定的锚点坐标 anchor 与给定的自身参考点 pivot 进行对齐处理, 如果 enable, 则将两个坐标进行对齐处理.
 */
public class LayoutParams {
    private boolean enable;
    @Nullable
    private Runnable changeListener;
    // 锚点相对于父元素左上角矩形的坐标, 0.0 是原点, 1.0 是边长 (w/h)
    @Getter
    private float anchorX;
    @Getter
    private float anchorY;

    // 自身参考点, (0.0, 0.0) 表示自身左上角, (1.0, 1.0) 表示自身右下角, (0.5, 0.5) 表示自身中心点
    @Getter
    private float pivotX;
    @Getter
    private float pivotY;

    public LayoutParams() {
        this.anchorX = 0.0f;
        this.anchorY = 0.0f;
        this.pivotX = 0.0f;
        this.pivotY = 0.0f;
    }

    public LayoutParams(float anchorX, float anchorY, float pivotX, float pivotY) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
    }

    public void set(float anchorX, float anchorY, float pivotX, float pivotY) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.notifyChanged();
    }

    public boolean isEnabled() {
        return this.enable;
    }

    public void enable() {
        this.enable = true;
        this.notifyChanged();
    }

    public void disable() {
        this.enable = false;
        this.notifyChanged();
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        this.notifyChanged();
    }

    public void setAnchor(float anchorX, float anchorY) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.notifyChanged();
    }

    public void setAnchorX(float anchorX) {
        this.anchorX = anchorX;
        this.notifyChanged();
    }

    public void setAnchorY(float anchorY) {
        this.anchorY = anchorY;
        this.notifyChanged();
    }

    public void setPivot(float pivotX, float pivotY) {
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.notifyChanged();
    }

    public void setPivotX(float pivotX) {
        this.pivotX = pivotX;
        this.notifyChanged();
    }

    public void setPivotY(float pivotY) {
        this.pivotY = pivotY;
        this.notifyChanged();
    }

    /**
     * 将元素中心对齐父元素中心.
     */
    public void center() {
        this.anchorX = 0.5f;
        this.anchorY = 0.5f;
        this.pivotX = 0.5f;
        this.pivotY = 0.5f;
        this.enable = true;
        this.notifyChanged();
    }

    /**
     * 将元素底边中心对齐父元素底边中心.
     */
    public void botton() {
        this.anchorX = 0.5f;
        this.anchorY = 1.0f;
        this.pivotX = 0.5f;
        this.pivotY = 1.0f;
        this.enable = true;
        this.notifyChanged();
    }

    /**
     * 将元素上边中心对齐父元素上边中心.
     */
    public void top() {
        this.anchorX = 0.5f;
        this.anchorY = 0.0f;
        this.pivotX = 0.5f;
        this.pivotY = 0.0f;
        this.enable = true;
        this.notifyChanged();
    }

    /**
     * 左对齐
     */
    public void left() {
        this.anchorX = 0.0f;
        this.anchorY = 0.5f;
        this.pivotX = 0.0f;
        this.pivotY = 0.5f;
        this.enable = true;
        this.notifyChanged();
    }

    /**
     * 右对齐
     */
    public void right() {
        this.anchorX = 1.0f;
        this.anchorY = 0.5f;
        this.pivotX = 1.0f;
        this.pivotY = 0.5f;
        this.enable = true;
        this.notifyChanged();
    }

    public void setChangeListener(@Nullable Runnable changeListener) {
        this.changeListener = changeListener;
    }

    private void notifyChanged() {
        if (this.changeListener != null) {
            this.changeListener.run();
        }
    }
}
