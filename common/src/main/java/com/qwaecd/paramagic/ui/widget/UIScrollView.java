package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.*;
import com.qwaecd.paramagic.ui.event.impl.WheelEvent;
import com.qwaecd.paramagic.ui.util.UILayout;

import javax.annotation.Nonnull;

public class UIScrollView extends UINode {
    /**
     * 视图偏移量, 可能是水平偏移量也可能是垂直偏移量
     */
    protected float viewOffset = 0.0f;
    /**
     * 是否是水平滚动视图<br>
     * true - 水平滚动视图<br>
     * false - 垂直滚动视图
     */
    protected boolean isHorizontal = false;
    /**
     * 滚动灵敏度
     */
    protected float sensitivity = 64.0f;
    /**
     * 子节点在滚动方向上的内容总长度（基于 layoutRect 计算）
     */
    protected float contentExtent = 0.0f;

    /**
     * 构造一个可以滚动的视图容器.
     * @param isHorizontal 是否是水平滚动视图
     */
    public UIScrollView(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
        this.clipMod = ClipMod.RECT;
    }

    @Override
    protected void onMouseScroll(UIEventContext<WheelEvent> context) {
        WheelEvent event = context.event;
        this.viewOffset += (float) event.scrollDelta * this.getSensitivity();
        this.clampViewOffset();
        this.requestLayout();
        context.consume();
    }

    /**
     * 根据子节点的 layoutRect 重新计算内容在滚动方向上的总长度.
     */
    protected void recalculateContentExtent() {
        float maxExtent = 0.0f;
        for (UINode child : this.children) {
            float end = this.isHorizontal
                    ? child.layoutRect.x + child.layoutRect.w
                    : child.layoutRect.y + child.layoutRect.h;
            if (end > maxExtent) {
                maxExtent = end;
            }
        }
        this.contentExtent = maxExtent;
    }

    /**
     * 将 viewOffset 限制在合法范围内.<br>
     * 上限为 0（列表顶部/左侧不会出现空白）,<br>
     * 下限为 min(0, viewportSize - contentExtent)（列表底部/右侧不会出现空白）.
     */
    protected void clampViewOffset() {
        float viewportSize = this.isHorizontal ? this.finalRect.w : this.finalRect.h;
        float minOffset = Math.min(0.0f, viewportSize - this.contentExtent);
        this.viewOffset = Math.max(minOffset, Math.min(0.0f, this.viewOffset));
    }

    @Override
    public void addChild(UINode child) {
        super.addChild(child);
        this.recalculateContentExtent();
    }

    @Override
    public void removeChild(UINode child) {
        super.removeChild(child);
        this.recalculateContentExtent();
    }

    /**
     * 计算滚动容器自身的自然尺寸。
     */
    @Override
    protected MeasureResult measureSelf(@Nonnull LayoutConstraints constraints) {
        float width = UILayout.resolveWidth(this.sizeMode, this.layoutRect, constraints.getMaxWidth());
        float height = UILayout.resolveHeight(this.sizeMode, this.layoutRect, constraints.getMaxHeight());
        return MeasureResult.of(width, height);
    }

    @Override
    protected void measureChildren(@Nonnull LayoutConstraints constraints) {
        LayoutConstraints childConstraints = LayoutConstraints.loose(this.measuredWidth, this.measuredHeight);
        for (UINode child : this.children) {
            child.measure(childConstraints);
        }
        this.recalculateContentExtent();
    }

    @Override
    protected void arrangeSelf(float parentX, float parentY, float parentW, float parentH) {
        super.arrangeSelf(parentX, parentY, parentW, parentH);
        this.clampViewOffset();
    }

    /**
     * 以当前 viewOffset 摆放所有子节点。
     */
    @Override
    protected void arrangeChildren() {
        float childParentX = this.finalRect.x + (this.isHorizontal ? this.viewOffset : 0);
        float childParentY = this.finalRect.y + (this.isHorizontal ? 0 : this.viewOffset);

        for (UINode child : this.children) {
            child.arrange(childParentX, childParentY, this.finalRect.w, this.finalRect.h);
        }
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public float getSensitivity() {
        return UIManager.hasCtrlKeyDown() ? this.sensitivity * 1.5f : this.sensitivity;
    }

    public float getViewOffset() {
        return this.viewOffset;
    }

    public void setViewOffset(float viewOffset) {
        this.viewOffset = viewOffset;
        this.requestLayout();
    }

    /**
     * 将屏幕坐标的 X 转换为视图坐标(元素本地坐标系)的 X
     */
    protected float screenToViewX(float screenX) {
        return screenX - this.finalRect.x - (this.isHorizontal ? this.viewOffset : 0);
    }

    /**
     * 将屏幕坐标的 Y 转换为视图坐标(元素本地坐标系)的 Y
     */
    protected float screenToViewY(float screenY) {
        return screenY - this.finalRect.y - (this.isHorizontal ? 0 : this.viewOffset);
    }

    /**
     * 将本地坐标系的 X 转换为屏幕坐标的 X
     */
    protected float viewToScreenX(float viewX) {
        return viewX + this.finalRect.x + (this.isHorizontal ? this.viewOffset : 0);
    }

    /**
     * 将本地坐标系的 Y 转换为屏幕坐标的 Y
     */
    protected float viewToScreenY(float viewY) {
        return viewY + this.finalRect.y + (this.isHorizontal ? 0 : this.viewOffset);
    }
}
