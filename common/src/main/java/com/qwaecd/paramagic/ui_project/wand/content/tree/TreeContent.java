package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui_project.wand.SpellTreeEditClientState;
import com.qwaecd.paramagic.network.packet.inventory.SpellTreeEditOperation;

import javax.annotation.Nonnull;
import java.util.List;

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

    private RootTreeNode rootTreeNode;

    public TreeContent(@Nonnull SpellTreeEditClientState editState) {
        super();
        this.editState = editState;
        this.rebuildTree();
    }

    public void onTreeDataRebuilt() {
        this.rebuildTree();
        this.requestMeasure();
    }

    private void onTreeOperationApplied(@Nonnull SpellTreeEditOperation operation) {
        this.rebuildTree();
        this.requestMeasure();
    }

    private void rebuildTree() {
        List<String> expandedPath = this.rootTreeNode == null
                ? List.of()
                : this.rootTreeNode.captureExpandedPathNodeIds();
        if (this.rootTreeNode != null) {
            this.removeChild(this.rootTreeNode);
        }
        // Rebuilds performed after attachment are data synchronization, not a new tree entrance.
        // The only full entrance animation is the initial tree attached to the UI.
        boolean playEntranceAnimation = this.getManager() == null;
        this.rootTreeNode = new RootTreeNode(this.editState, this::onTreeOperationApplied, playEntranceAnimation);
        this.addChild(this.rootTreeNode);
        this.rootTreeNode.restoreExpandedPathNodeIds(expandedPath);
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
