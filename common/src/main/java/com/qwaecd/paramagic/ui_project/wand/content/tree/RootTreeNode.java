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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RootTreeNode extends TreeNode {

    private float renderAlpha = 0.0f;

    public RootTreeNode() {
        super();
        this.state = SubTreeState.EXPANDED;

        this.addListener(AllUIEvents.MOUSE_CLICK, EventPhase.BUBBLING, this::handleChildClick);
        this.addListener(AllUIEvents.MOUSE_DOUBLE_CLICK, EventPhase.BUBBLING, this::handleChildDoubleClick);
    }

    @Override
    protected void onAttached(@NotNull UIManager manager) {
        this.animateFloat(
                this.renderAlpha,
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
        if (targetNode.canHitAppendNodeRight((float) event.mouseX, (float) event.mouseY)) {
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

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        float w = WEAssets.ITEM_RECT.width;
        float h = WEAssets.ITEM_RECT.height;
        if (this.subNode.isEmpty()) {
            w += WEAssets.ADD_NODE_RIGHT.width + ADD_NODE_GAP;
        }
        return MeasureResult.of(w, h);
    }

    @Override
    protected void arrangeChildren() {
        float offsetX = this.getMeasuredWidth() + ITEM_RECT_GAP;
        for (TreeNode node : this.subNode) {
            Rect rect = node.getLayoutRect();
            rect.set(
                    offsetX, 0.0f,
                    node.getMeasuredWidth(), node.getMeasuredHeight()
            );
            node.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
            offsetX += rect.w + ITEM_RECT_GAP;
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
