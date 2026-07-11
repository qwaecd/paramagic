package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UINode;

import javax.annotation.Nonnull;

/**
 * 顺序布局容器，按水平或垂直方向依次摆放子节点。
 * <p>
 * 适用于工具栏、表单行、菜单项列表等线性布局场景。容器会根据子节点的测量尺寸、
 * padding 和 gap 计算自身自然尺寸，并在 arrange 阶段写入子节点的布局位置。
 */
public class UIStackPanel extends UINode {
    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    @Nonnull
    private Direction direction;
    private float gap;
    private float paddingLeft;
    private float paddingTop;
    private float paddingRight;
    private float paddingBottom;

    public UIStackPanel() {
        this(Direction.VERTICAL);
    }

    public UIStackPanel(@Nonnull Direction direction) {
        this.direction = direction;
    }

    @Override
    @Nonnull
    public MeasureResult measure(@Nonnull LayoutConstraints constraints) {
        LayoutConstraints contentConstraints = LayoutConstraints.loose(
                Math.max(0.0f, constraints.getMaxWidth() - this.paddingLeft - this.paddingRight),
                Math.max(0.0f, constraints.getMaxHeight() - this.paddingTop - this.paddingBottom)
        );

        float contentWidth = 0.0f;
        float contentHeight = 0.0f;
        int measuredChildren = 0;

        for (UINode child : this.children) {
            MeasureResult childResult = child.measure(contentConstraints);
            if (this.direction == Direction.HORIZONTAL) {
                contentWidth += childResult.getWidth();
                contentHeight = Math.max(contentHeight, childResult.getHeight());
            } else {
                contentWidth = Math.max(contentWidth, childResult.getWidth());
                contentHeight += childResult.getHeight();
            }
            measuredChildren++;
        }

        if (measuredChildren > 1) {
            float totalGap = this.gap * (measuredChildren - 1);
            if (this.direction == Direction.HORIZONTAL) {
                contentWidth += totalGap;
            } else {
                contentHeight += totalGap;
            }
        }

        float naturalWidth = contentWidth + this.paddingLeft + this.paddingRight;
        float naturalHeight = contentHeight + this.paddingTop + this.paddingBottom;
        float width = this.resolveMeasuredWidth(naturalWidth, constraints);
        float height = this.resolveMeasuredHeight(naturalHeight, constraints);
        MeasureResult result = MeasureResult.of(width, height);

        this.setMeasureResult(result);
        this.layoutRect.setWH(result.getWidth(), result.getHeight());
        this.measureDirty = false;
        return result;
    }

    private float resolveMeasuredWidth(float naturalWidth, LayoutConstraints constraints) {
        return switch (this.sizeMode) {
            case FILL, FILL_WIDTH -> constraints.getMaxWidth();
            case FIXED, FILL_HEIGHT -> naturalWidth;
        };
    }

    private float resolveMeasuredHeight(float naturalHeight, LayoutConstraints constraints) {
        return switch (this.sizeMode) {
            case FILL, FILL_HEIGHT -> constraints.getMaxHeight();
            case FIXED, FILL_WIDTH -> naturalHeight;
        };
    }

    @Override
    protected void arrangeChildren() {
        float cursorX = this.paddingLeft;
        float cursorY = this.paddingTop;

        for (UINode child : this.children) {
            child.layoutRect.set(cursorX, cursorY, child.getMeasuredWidth(), child.getMeasuredHeight());
            child.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);

            if (this.direction == Direction.HORIZONTAL) {
                cursorX += child.getMeasuredWidth() + this.gap;
            } else {
                cursorY += child.getMeasuredHeight() + this.gap;
            }
        }
    }

    @Nonnull
    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(@Nonnull Direction direction) {
        this.direction = direction;
        this.requestMeasure();
    }

    public float getGap() {
        return this.gap;
    }

    public void setGap(float gap) {
        this.gap = Math.max(0.0f, gap);
        this.requestMeasure();
    }

    public void setPadding(float padding) {
        this.setPadding(padding, padding, padding, padding);
    }

    public void setPadding(float horizontal, float vertical) {
        this.setPadding(horizontal, vertical, horizontal, vertical);
    }

    public void setPadding(float left, float top, float right, float bottom) {
        this.paddingLeft = Math.max(0.0f, left);
        this.paddingTop = Math.max(0.0f, top);
        this.paddingRight = Math.max(0.0f, right);
        this.paddingBottom = Math.max(0.0f, bottom);
        this.requestMeasure();
    }

    public float getPaddingLeft() {
        return this.paddingLeft;
    }

    public float getPaddingTop() {
        return this.paddingTop;
    }

    public float getPaddingRight() {
        return this.paddingRight;
    }

    public float getPaddingBottom() {
        return this.paddingBottom;
    }
}
