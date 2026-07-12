package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.network.packet.inventory.SpellTreeEditOperation;
import com.qwaecd.paramagic.thaumaturgy.spelltree.SpellNodeData;
import com.qwaecd.paramagic.ui.api.TooltipContent;
import com.qwaecd.paramagic.ui.api.TooltipQuery;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui_project.wand.SpellTreeEditClientState;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class RootTreeNode extends TreeNode {
    @Nonnull
    private final SpellTreeEditClientState editState;
    @Nonnull
    private final Consumer<SpellTreeEditOperation> treeChanged;
    private final boolean playEntranceAnimation;

    private static final TooltipContent appendNodeRightTipRoot = TooltipContent.of(
            Component.translatable("gui.paramagic.spell_edit_table.para_tree.tooltip.add_node")
                    .withStyle(ChatFormatting.GREEN)
    );

    public RootTreeNode(
            @Nonnull SpellTreeEditClientState editState,
            @Nonnull Consumer<SpellTreeEditOperation> treeChanged,
            boolean playEntranceAnimation
    ) {
        super(editState.getTreeData().getRoot().getNodeId(), editState.getTreeData().getRoot().getOperatorId(), playEntranceAnimation);
        this.editState = editState;
        this.treeChanged = treeChanged;
        this.playEntranceAnimation = playEntranceAnimation;
        this.state = SubTreeState.EXPANDED;

        this.appendDataChildren(editState.getTreeData().getRoot());
        this.applyExpandedPath(List.of(this), 0);

        this.addListener(AllUIEvents.MOUSE_CLICK, EventPhase.BUBBLING, this::handleChildClick);
        this.addListener(AllUIEvents.MOUSE_DOUBLE_CLICK, EventPhase.BUBBLING, this::handleChildDoubleClick);
    }

    @Override
    public @Nullable TooltipContent getTooltip(@NotNull TooltipQuery query) {
        final float x = query.mouseX();
        final float y = query.mouseY();
        if (this.nodeItemRect.contains(x, y)) {
            return UINode.getTooltipFromItem(this.renderingItem);
        }
        if (this.deleteSubTreeRect.contains(x, y) && this.state == SubTreeState.EXPANDED) {
            return deleteSubTreeTip;
        }
        if (this.appendNodeRectRight.contains(x,y)) {
            return appendNodeRightTipRoot;
        }
        if (this.appendNodeRectDown.contains(x, y)) {
            return appendNodeDownTip;
        }
        return null;
    }

    private void appendDataChildren(@Nonnull SpellNodeData parentData) {
        for (SpellNodeData childData : parentData.getChildren()) {
            this.appendTreeNode(this.createNode(childData));
        }
    }

    @Nonnull
    private TreeNode createNode(@Nonnull SpellNodeData data) {
        TreeNode node = new TreeNode(data.getNodeId(), data.getOperatorId(), this.playEntranceAnimation);
        for (SpellNodeData childData : data.getChildren()) {
            node.appendTreeNode(this.createNode(childData));
        }
        return node;
    }

    private void handleChildClick(UIEventContext<MouseClick> context) {
        UINode targetNode = context.getTargetNode();
        if (targetNode instanceof TreeNode treeNode) {
            this.clickedTreeNode(treeNode, context);
        } else if (targetNode instanceof HiddenSubTreeNode hiddenNode) {
            this.clickedHiddenNode(hiddenNode, context);
        }
        context.consume();
    }

    private void clickedTreeNode(TreeNode targetNode, UIEventContext<MouseClick> context) {
        MouseClick event = context.event;
        if (targetNode.canHitDeleteNode((float) event.mouseX, (float) event.mouseY)) {
            this.submit(SpellTreeEditOperation.DELETE_SUBTREE, targetNode.getNodeId(), 0);
        } else if (targetNode.canHitAppendNodeRight((float) event.mouseX, (float) event.mouseY)) {
            if (targetNode.getParent() instanceof TreeNode treeNode) {
                this.submit(SpellTreeEditOperation.ADD_CHILD, treeNode.getNodeId(), treeNode.getChildCount());
            } else if (targetNode == this) {
                this.submit(SpellTreeEditOperation.ADD_CHILD, this.getNodeId(), this.getChildCount());
            }
        } else if (targetNode.canHitAppendNodeDown((float) event.mouseX, (float) event.mouseY)) {
            this.submit(SpellTreeEditOperation.ADD_CHILD, targetNode.getNodeId(), 0);
        } else if (targetNode.canHitDeleteSubTree((float) event.mouseX, (float) event.mouseY)) {
            this.submit(SpellTreeEditOperation.CLEAR_CHILDREN, targetNode.getNodeId(), 0);
        } else if (targetNode.canHitInteractNode((float) event.mouseX, (float) event.mouseY) && targetNode.getNodeId() != null) {
            this.submit(SpellTreeEditOperation.INTERACT_NODE_OPERATOR, targetNode.getNodeId(), 0);
        }
    }

    private void submit(@Nonnull SpellTreeEditOperation operation, String nodeId, int childIndex) {
        if (nodeId != null && this.editState.submit(operation, nodeId, childIndex)) {
            this.treeChanged.accept(operation);
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

    @Nonnull
    List<String> captureExpandedPathNodeIds() {
        List<String> result = new ArrayList<>();
        TreeNode current = this;
        while (current != null) {
            String nodeId = current.getNodeId();
            if (nodeId == null) {
                break;
            }
            result.add(nodeId);
            current = current.expandedSubNode;
        }
        return result;
    }

    void restoreExpandedPathNodeIds(@Nonnull List<String> nodeIds) {
        if (nodeIds.isEmpty() || !nodeIds.get(0).equals(this.getNodeId())) {
            return;
        }
        List<TreeNode> path = new ArrayList<>();
        TreeNode current = this;
        path.add(current);
        for (int i = 1; i < nodeIds.size(); i++) {
            TreeNode next = null;
            for (TreeNode child : current.subNode) {
                if (nodeIds.get(i).equals(child.getNodeId())) {
                    next = child;
                    break;
                }
            }
            if (next == null) {
                break;
            }
            path.add(next);
            current = next;
        }
        this.restoreExpandedPathImmediately(path, 0);
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
        float alpha = this.getEffectiveRenderAlpha();
        if (alpha <= 0.0f) {
            return;
        }
        context.renderSpriteWithAlpha(WEAssets.ITEM_RECT, this.finalRect.x, this.finalRect.y, alpha);
        if (!this.renderingItem.isEmpty() && alpha >= ITEM_VISIBILITY_ALPHA_THRESHOLD) {
            final float xOffset = 3.0f;
            final float yOffset = 3.0f;
            context.renderItem(this.renderingItem, (int) (this.finalRect.x + xOffset), (int) (this.finalRect.y + yOffset));
            context.renderItemDecorations(this.renderingItem, (int) (this.finalRect.x + xOffset), (int) (this.finalRect.y + yOffset));
        }
        if (this.isLastNode()) {
            context.renderSpriteWithAlpha(
                    WEAssets.ADD_NODE_RIGHT,
                    this.finalRect.x + WEAssets.ITEM_RECT.width + ADD_NODE_GAP,
                    this.finalRect.y + (WEAssets.ITEM_RECT.height - WEAssets.ADD_NODE_RIGHT.height) / 2.0f,
                    alpha
            );
        }
    }
}
