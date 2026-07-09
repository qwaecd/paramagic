package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui_project.wand.SpellTreeEditClientState;

import javax.annotation.Nonnull;

public final class TreeContent extends UINode {
    // 内容左侧留白，也决定小树水平拖动时左边的冗余空间。
    private static final float CONTENT_PADDING_LEFT = 55.0f;
    // 内容顶部留白，也决定小树垂直拖动时上方的冗余空间。
    private static final float CONTENT_PADDING_TOP = 45.0f;
    // 内容右侧留白，也决定小树水平拖动时右边的冗余空间。
    private static final float CONTENT_PADDING_RIGHT = 55.0f;
    // 内容底部留白，也决定小树垂直拖动时下方的冗余空间。
    private static final float CONTENT_PADDING_BOTTOM = 45.0f;

    @Nonnull
    private final SpellTreeEditClientState editState;

    private final RootTreeNode rootTreeNode;

    public TreeContent(@Nonnull SpellTreeEditClientState editState) {
        super();
        this.editState = editState;
        this.rootTreeNode = new RootTreeNode();

        this.addChild(this.rootTreeNode);
    }

    public void onTreeDataRebuilt() {
        this.requestMeasure();
    }

    @Override
    @Nonnull
    public MeasureResult measure(@Nonnull LayoutConstraints constraints) {
        this.rootTreeNode.measure(constraints);

        float naturalWidth = CONTENT_PADDING_LEFT + this.rootTreeNode.getMeasuredWidth() + CONTENT_PADDING_RIGHT;
        float naturalHeight = CONTENT_PADDING_TOP + this.rootTreeNode.getMeasuredHeight() + CONTENT_PADDING_BOTTOM;
        float minWidth = constraints.getMaxWidth() + CONTENT_PADDING_LEFT + CONTENT_PADDING_RIGHT;
        float minHeight = constraints.getMaxHeight() + CONTENT_PADDING_TOP + CONTENT_PADDING_BOTTOM;

        MeasureResult result = MeasureResult.of(
                Math.max(naturalWidth, minWidth),
                Math.max(naturalHeight, minHeight)
        );
        this.setMeasureResult(result);
        this.measureDirty = false;
        return result;
    }

    @Override
    protected void arrangeChildren() {
        this.rootTreeNode.getLayoutRect().set(
                CONTENT_PADDING_LEFT,
                CONTENT_PADDING_TOP,
                this.rootTreeNode.getMeasuredWidth(),
                this.rootTreeNode.getMeasuredHeight()
        );
        this.rootTreeNode.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
    }
}
