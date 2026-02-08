package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.WheelEvent;
import com.qwaecd.paramagic.ui.util.UILayout;

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
    protected float sensitivity = 16.0f;
    /**
     * 子节点在滚动方向上的内容总长度（基于 localRect 计算）
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
        this.viewOffset += (float) event.scrollDelta * this.sensitivity;
        this.clampViewOffset();
        this.layoutChildren();
        context.consume();
    }

    /**
     * 根据子节点的 localRect 重新计算内容在滚动方向上的总长度.
     */
    protected void recalculateContentExtent() {
        float maxExtent = 0.0f;
        for (UINode child : this.children) {
            float end = this.isHorizontal
                    ? child.getLocalRect().x + child.getLocalRect().w
                    : child.getLocalRect().y + child.getLocalRect().h;
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
        float viewportSize = this.isHorizontal ? this.worldRect.w : this.worldRect.h;
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
     * 计算此节点及其子节点的屏幕绝对坐标.<br>
     * 先布局自身的 worldRect, 再 clamp viewOffset, 最后以偏移量布局子节点.
     */
    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        UILayout.layout(this.localRect, this.worldRect, this.layoutParams, this.sizeMode, parentX, parentY, parentW, parentH);
        this.clampViewOffset();
        this.layoutChildren();
    }

    /**
     * 以当前 viewOffset 重新布局所有子节点.<br>
     * 滚动时只需调用此方法即可更新子树的 worldRect, 无需重新布局整棵树.
     */
    protected void layoutChildren() {
        float childParentX = this.worldRect.x + (this.isHorizontal ? this.viewOffset : 0);
        float childParentY = this.worldRect.y + (this.isHorizontal ? 0 : this.viewOffset);

        for (UINode child : this.children) {
            child.layout(childParentX, childParentY, this.worldRect.w, this.worldRect.h);
        }
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public float getSensitivity() {
        return this.sensitivity;
    }

    public float getViewOffset() {
        return this.viewOffset;
    }

    public void setViewOffset(float viewOffset) {
        this.viewOffset = viewOffset;
    }

    /**
     * 将屏幕坐标的 X 转换为视图坐标(元素本地坐标系)的 X
     */
    protected float screenToViewX(float screenX) {
        return screenX - this.worldRect.x - (this.isHorizontal ? this.viewOffset : 0);
    }

    /**
     * 将屏幕坐标的 Y 转换为视图坐标(元素本地坐标系)的 Y
     */
    protected float screenToViewY(float screenY) {
        return screenY - this.worldRect.y - (this.isHorizontal ? 0 : this.viewOffset);
    }

    /**
     * 将本地坐标系的 X 转换为屏幕坐标的 X
     */
    protected float viewToScreenX(float viewX) {
        return viewX + this.worldRect.x + (this.isHorizontal ? this.viewOffset : 0);
    }

    /**
     * 将本地坐标系的 Y 转换为屏幕坐标的 Y
     */
    protected float viewToScreenY(float viewY) {
        return viewY + this.worldRect.y + (this.isHorizontal ? 0 : this.viewOffset);
    }
}
