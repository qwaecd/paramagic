package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.ui.core.ClipMod;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.Rect;

import javax.annotation.Nonnull;

public final class TreeScrollViewport extends UINode {
    private final TreeContent treeContent;
    private final Rect clipRect = new Rect();

    private float scrollX = 0.0f;
    private float scrollY = 0.0f;

    public TreeScrollViewport() {
        super();
        this.treeContent = new TreeContent();
        this.clipMod = ClipMod.RECT;
        this.addChild(treeContent);
    }

    @Nonnull
    TreeContent getTreeContent() {
        return this.treeContent;
    }

    public float getScrollX() {
        return this.scrollX;
    }

    public float getScrollY() {
        return this.scrollY;
    }

    public float getContentWidth() {
        return this.treeContent.getMeasuredWidth();
    }

    public float getContentHeight() {
        return this.treeContent.getMeasuredHeight();
    }

    public void setScroll(float scrollX, float scrollY) {
        this.scrollX = Math.max(0.0f, scrollX);
        this.scrollY = Math.max(0.0f, scrollY);
        this.requestLayout();
    }

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        return MeasureResult.of(
                Math.max(0.0f, constraints.getMaxWidth()),
                Math.max(0.0f, constraints.getMaxHeight())
        );
    }

    @Override
    protected void arrangeSelf(float parentX, float parentY, float parentW, float parentH) {
        super.arrangeSelf(parentX, parentY, parentW, parentH);
        this.clipRect.set(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
    }

    @Override
    protected void arrangeChildren() {
        float maxScrollX = Math.max(0.0f, this.treeContent.getMeasuredWidth() - this.finalRect.w);
        float maxScrollY = Math.max(0.0f, this.treeContent.getMeasuredHeight() - this.finalRect.h);
        this.scrollX = Math.min(this.scrollX, maxScrollX);
        this.scrollY = Math.min(this.scrollY, maxScrollY);

        Rect contentRect = this.treeContent.getLayoutRect();
        contentRect.set(
                -this.scrollX,
                -this.scrollY,
                this.treeContent.getMeasuredWidth(),
                this.treeContent.getMeasuredHeight()
        );
        this.treeContent.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
    }

    @Override
    @Nonnull
    protected Rect getClipRect() {
        return this.clipRect;
    }
}
