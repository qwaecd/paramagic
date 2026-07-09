package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RootTreeNode extends TreeNode {

    public RootTreeNode() {
        super();
        this.state = SubTreeState.EXPANDED;
        this.renderAlpha = 0.0f;

        this.addListener(AllUIEvents.MOUSE_CLICK, EventPhase.BUBBLING, this::handleChildClick);
        this.addListener(AllUIEvents.MOUSE_DOUBLE_CLICK, EventPhase.BUBBLING, this::handleChildDoubleClick);
    }

    @Override
    protected void onAttached(@NotNull UIManager manager) {
        this.animateFloat(
                0.0f,
                1.0f,
                0.4f,
                EasingFunction.easeInOutQuad,
                Interpolation::linear,
                (v -> this.renderAlpha = v)
        ).setDelay(0.3f);
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        this.handleChildClick(context);
    }

    private void handleChildClick(UIEventContext<MouseClick> context) {
        UINode targetNode = context.getTargetNode();
        if (targetNode instanceof TreeNode treeNode) {
            this.clickedTreeNode(treeNode, context);
        } else if (targetNode instanceof HiddenSubTreeNode hiddenNode) {
            this.clickedHiddenNode(hiddenNode, context);
        }
    }

    private void clickedTreeNode(TreeNode targetNode, UIEventContext<MouseClick> context) {
        MouseClick event = context.event;
        if (targetNode.canHitDeleteNode((float) event.mouseX, (float) event.mouseY)) {
            this.deleteNode(targetNode);
            context.consume();
        } else if (targetNode.canHitAppendNodeRight((float) event.mouseX, (float) event.mouseY)) {
            if (targetNode.getParent() instanceof TreeNode treeNode) {
                TreeNode newNode = new TreeNode();
                treeNode.appendTreeNode(newNode);
            } else if (targetNode == this) {
                TreeNode newNode = new TreeNode();
                this.appendTreeNode(newNode);
            }
            context.consume();
        } else if (targetNode.canHitAppendNodeDown((float) event.mouseX, (float) event.mouseY)) {
            TreeNode newNode = new TreeNode();
            targetNode.createSubTree(newNode);
            this.expandToNode(targetNode);
            context.consume();
        } else if (targetNode.canHitDeleteSubTree((float) event.mouseX, (float) event.mouseY)) {
            this.deleteSubTreeOf(targetNode instanceof TreeNode ? targetNode : null);
            context.consume();
        }
    }

    private void clickedHiddenNode(HiddenSubTreeNode hiddenNode, UIEventContext<MouseClick> context) {
        if (hiddenNode.getParent() instanceof TreeNode treeNode) {
            this.expandToNode(treeNode);
        }
        context.consume();
    }

    private void handleChildDoubleClick(UIEventContext<DoubleClick> context) {
        this.handleChildClick(UIEventContext.upcast(AllUIEvents.MOUSE_CLICK, context));
    }

    @Override
    public boolean isLastNode() {
        return this.subNode.isEmpty();
    }

    void expandToNode(TreeNode node) {
        if (!this.containsInSubtree(node)) {
            return;
        }

        List<TreeNode> expandedPath = new ArrayList<>();
        UINode current = node;
        while (current instanceof TreeNode treeNode) {
            expandedPath.add(treeNode);
            if (treeNode == this) {
                break;
            }
            current = treeNode.getParent();
        }
        Collections.reverse(expandedPath);

        if (expandedPath.isEmpty() || expandedPath.get(0) != this) {
            return;
        }

        this.applyExpandedPath(expandedPath, 0);
        this.requestMeasure();
    }

    boolean deleteSubTreeOf(TreeNode parent) {
        if (!this.containsInSubtree(parent)) {
            return false;
        }
        if (!parent.deleteSubTree()) {
            return false;
        }
        this.expandToNode(parent);
        return true;
    }

    boolean deleteNode(TreeNode node) {
        if (node == this || !this.containsInSubtree(node) || !(node.getParent() instanceof TreeNode parent)) {
            return false;
        }
        if (!node.deleteFromParent()) {
            return false;
        }
        this.expandToNode(parent);
        return true;
    }

    @Override
    protected void setSubTreeAddable() {
        // root 不应该变成 addable 状态
        this.state = SubTreeState.EXPANDED;
    }

    @Override
    @Nonnull
    protected MeasureResult measureTreeNode() {
        float ownWidth = this.getOwnNodeWidth();
        if (this.subNode.isEmpty()) {
            return MeasureResult.of(ownWidth, WEAssets.ITEM_RECT.height);
        }

        float childrenWidth = this.getVisibleChildrenExtentWidth();
        float childrenHeight = this.getVisibleChildrenMaxHeight();
        return MeasureResult.of(
                ownWidth + ITEM_RECT_GAP + childrenWidth,
                Math.max(WEAssets.ITEM_RECT.height, childrenHeight)
        );
    }

    @Override
    protected void arrangeChildren() {
        float offsetX = this.getOwnNodeWidth() + ITEM_RECT_GAP;
        for (TreeNode node : this.subNode) {
            Rect rect = node.getLayoutRect();
            rect.set(
                    offsetX, 0.0f,
                    node.getOwnNodeWidth(), node.getMeasuredHeight()
            );
            node.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
            offsetX += node.getOwnNodeWidth() + ITEM_RECT_GAP;
        }
    }

    @Override
    protected void render(@NotNull UIRenderContext context) {
        if (this.renderAlpha <= 0.0f) {
            return;
        }
        context.renderSpriteWithAlpha(WEAssets.ITEM_RECT, this.finalRect.x, this.finalRect.y, this.renderAlpha);
        if (this.isLastNode()) {
            context.renderSpriteWithAlpha(
                    WEAssets.ADD_NODE_RIGHT,
                    this.finalRect.x + WEAssets.ITEM_RECT.width + ADD_NODE_GAP,
                    this.finalRect.y + (WEAssets.ITEM_RECT.height - WEAssets.ADD_NODE_RIGHT.height) / 2.0f,
                    this.renderAlpha
            );
        }
    }
}
