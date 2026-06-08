package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode extends UINode {
    protected final List<TreeNode> subNode = new ArrayList<>();

    protected static final float CHAIN_Y_GAP = 1.0f;
    protected static final float ADD_NODE_GAP = 1.0f;
    protected static final float BETWEEN_NODE_GAP = 1.0f;
    protected static final float ITEM_RECT_GAP = 2.0f;

    @Nonnull
    protected SubTreeState state;

    @Nullable
    protected TreeNode expandedSubNode = null;

    protected final HiddenSubTreeNode hiddenSubTreeNode;

    protected boolean isLastNode = true;

    protected final Rect appendNodeRectRight = new Rect();
    protected final Rect appendNodeRectDown = new Rect();

    public TreeNode() {
        super();
        this.state = SubTreeState.ADDABLE;
        this.hiddenSubTreeNode = new HiddenSubTreeNode(this);
        this.hiddenSubTreeNode.disable();
        this.addChild(this.hiddenSubTreeNode);
    }

    public boolean canHitAppendNodeRight(float mouseX, float mouseY) {
        return this.isLastNode() && this.appendNodeRectRight.contains(mouseX, mouseY);
    }

    public boolean canHitAppendNodeDown(float mouseX, float mouseY) {
        return this.state == SubTreeState.ADDABLE && this.appendNodeRectDown.contains(mouseX, mouseY);
    }

    public void createSubTree(TreeNode subNode) {
        this.subNode.add(subNode);
        this.addChild(subNode);
    }

    public void appendTreeNode(TreeNode node) {
        if (!this.subNode.isEmpty()) {
            TreeNode sub = this.subNode.get(this.subNode.size() - 1);
            sub.setLastNode(false);
        }
        this.subNode.add(node);
        node.setLastNode(true);
        this.addChild(node);
    }

    protected void applyExpandedPath(@Nonnull List<TreeNode> expandedPath, int pathIndex) {
        if (pathIndex < 0 || pathIndex >= expandedPath.size() || expandedPath.get(pathIndex) != this) {
            return;
        }

        this.hiddenSubTreeNode.disable();
        if (this.subNode.isEmpty()) {
            this.expandedSubNode = null;
            this.state = SubTreeState.ADDABLE;
            return;
        }

        TreeNode nextExpandedNode = null;
        if (pathIndex + 1 < expandedPath.size()) {
            nextExpandedNode = expandedPath.get(pathIndex + 1);
        }

        this.expandedSubNode = nextExpandedNode;
        this.state = SubTreeState.EXPANDED;

        for (TreeNode child : this.subNode) {
            child.enableWithoutLayout();
            if (child == nextExpandedNode) {
                child.applyExpandedPath(expandedPath, pathIndex + 1);
            } else {
                child.collapseForSingleExpandedPath();
            }
        }
    }

    protected void collapseForSingleExpandedPath() {
        this.expandedSubNode = null;

        if (this.subNode.isEmpty()) {
            this.state = SubTreeState.ADDABLE;
            this.hiddenSubTreeNode.disable();
            return;
        }

        this.state = SubTreeState.HIDDEN;
        this.hiddenSubTreeNode.enable();

        for (TreeNode child : this.subNode) {
            child.collapseForSingleExpandedPath();
            child.disableWithoutLayout();
        }
    }

    void setLastNode(boolean isLastNode) {
        this.isLastNode = isLastNode;
    }

    public boolean isLastNode() {
        return this.isLastNode;
    }

    protected void enableWithoutLayout() {
        this.visible = true;
        this.hitTestable = true;
    }

    protected void disableWithoutLayout() {
        this.visible = false;
        this.hitTestable = false;
    }

    @Override
    public MeasureResult measure(LayoutConstraints constraints) {
        return super.measure(constraints);
    }

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        float w, h;
        switch (this.state) {
            case HIDDEN -> {
                w = WEAssets.ITEM_RECT.width;
                h = WEAssets.ITEM_RECT.height + CHAIN_Y_GAP * 2 + WEAssets.CHAIN.height + WEAssets.HIDDEN_NODE.height;
            }
            case EXPANDED -> {
                w = WEAssets.ITEM_RECT.width;
                h = WEAssets.ITEM_RECT.height + CHAIN_Y_GAP * 2 + WEAssets.CHAIN.height * 3 + BETWEEN_NODE_GAP * 2;
            }
            case ADDABLE -> {
                w = WEAssets.ITEM_RECT.width;
                h = WEAssets.ITEM_RECT.height + WEAssets.ADD_NODE_DOWN.height + ADD_NODE_GAP;
            }
            default -> {
                w = WEAssets.ITEM_RECT.width;
                h = WEAssets.ITEM_RECT.height;
            }
        }
        if (this.isLastNode()) {
            w += WEAssets.ADD_NODE_RIGHT.width + ADD_NODE_GAP;
        }
        return MeasureResult.of(w, h);
    }

    @Override
    protected void arrangeSelf(float parentX, float parentY, float parentW, float parentH) {
        super.arrangeSelf(parentX, parentY, parentW, parentH);
        if (this.isLastNode()) {
            this.appendNodeRectRight.set(
                    this.finalRect.x + WEAssets.ITEM_RECT.width + ADD_NODE_GAP,
                    this.finalRect.y + (WEAssets.ITEM_RECT.height - WEAssets.ADD_NODE_RIGHT.height) / 2.0f,
                    WEAssets.ADD_NODE_RIGHT.width,
                    WEAssets.ADD_NODE_RIGHT.height
            );
        } else {
            this.appendNodeRectRight.set(0.0f, 0.0f, 0.0f, 0.0f);
        }
        switch (this.state) {
            case HIDDEN, EXPANDED -> this.appendNodeRectDown.set(0.0f, 0.0f, 0.0f, 0.0f);
            case ADDABLE -> this.appendNodeRectDown.set(
                    this.finalRect.x + (WEAssets.ITEM_RECT.width - WEAssets.ADD_NODE_DOWN.width) / 2.0f,
                    this.finalRect.y + WEAssets.ITEM_RECT.height + ADD_NODE_GAP,
                    WEAssets.ADD_NODE_DOWN.width,
                    WEAssets.ADD_NODE_DOWN.height
            );
        }
    }

    @Override
    protected void arrangeChildren() {
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        switch (this.state) {
            case HIDDEN -> offsetY = WEAssets.ITEM_RECT.height + CHAIN_Y_GAP * 2 + WEAssets.CHAIN.height;
            case EXPANDED -> offsetY = WEAssets.ITEM_RECT.height + CHAIN_Y_GAP * 2 + WEAssets.CHAIN.height * 3 + BETWEEN_NODE_GAP * 2;
            case ADDABLE -> offsetY = WEAssets.ITEM_RECT.height + CHAIN_Y_GAP + WEAssets.ADD_NODE_DOWN.height;
        }

        Rect rect = this.hiddenSubTreeNode.getLayoutRect();
        rect.set(offsetX, offsetY, this.hiddenSubTreeNode.getMeasuredWidth(), this.hiddenSubTreeNode.getMeasuredHeight());
        this.hiddenSubTreeNode.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);

        for (TreeNode treeNode : this.subNode) {
            Rect layoutRect = treeNode.getLayoutRect();
            layoutRect.set(offsetX, offsetY, treeNode.getMeasuredWidth(), treeNode.getMeasuredHeight());
            treeNode.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
            offsetX += treeNode.getMeasuredWidth() + ITEM_RECT_GAP;
        }
    }

    @Override
    protected void render(@NotNull UIRenderContext context) {
        float x = this.finalRect.x;
        float y = this.finalRect.y;
        context.renderSprite(WEAssets.ITEM_RECT, x, y);
        y += WEAssets.ITEM_RECT.height + CHAIN_Y_GAP;
        switch (this.state) {
            case EXPANDED -> {
                x += (WEAssets.ITEM_RECT.width - WEAssets.CHAIN.width) / 2.0f;
                for (int i = 0; i < 3; i++) {
                    context.renderSprite(WEAssets.CHAIN, x, y);
                    y += WEAssets.CHAIN.height + BETWEEN_NODE_GAP;
                }
            }
            case HIDDEN -> {
                x += (WEAssets.ITEM_RECT.width - WEAssets.CHAIN.width) / 2.0f;
                context.renderSprite(WEAssets.CHAIN, x, y);
            }
            case ADDABLE -> {
                x += (WEAssets.ITEM_RECT.width - WEAssets.ADD_NODE_DOWN.width) / 2.0f;
                context.renderSprite(WEAssets.ADD_NODE_DOWN, x, y);
            }
        }
        if (this.isLastNode()) {
            context.renderSprite(WEAssets.ADD_NODE_RIGHT, this.appendNodeRectRight.x, this.appendNodeRectRight.y);
        }
    }

    @Override
    public void renderDebug(@NotNull UIRenderContext context) {
        super.renderDebug(context);
        context.renderOutline(this.appendNodeRectRight, UIColor.BLUE);
        context.renderOutline(this.appendNodeRectDown, UIColor.BLUE);
    }

    @Nonnull
    public SubTreeState getState() {
        return this.state;
    }

    public enum SubTreeState {
        EXPANDED,   // 真实展开
        HIDDEN,     // 折叠为占位节点
        ADDABLE     // 可添加子节点
    }
}
