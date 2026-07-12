package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.api.TooltipContent;
import com.qwaecd.paramagic.ui.api.TooltipQuery;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.*;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode extends UINode {
    @Nullable
    private final String nodeId;
    @Nullable
    private final ParaOpId operatorId;
    @Nonnull
    protected final ItemStack renderingItem;
    protected final List<TreeNode> subNode = new ArrayList<>();

    protected static final float CHAIN_Y_GAP = 1.0f;
    protected static final float ADD_NODE_GAP = 1.0f;
    protected static final float BETWEEN_NODE_GAP = 1.0f;
    protected static final float ITEM_RECT_GAP = 2.0f;
    private static final String REVEAL_ANIMATION_KEY = "TreeNodeReveal";
    private static final float MIN_REVEAL_ALPHA = 0.1f;
    /**
     * Items do not fade. They become visible only after the shared node chrome alpha has
     * reached this value, so an operator never appears before its item cell.
     */
    protected static final float ITEM_VISIBILITY_ALPHA_THRESHOLD = 0.3f;

    @Nonnull
    protected SubTreeState state;

    @Nullable
    protected TreeNode expandedSubNode = null;

    protected final HiddenSubTreeNode hiddenSubTreeNode;

    protected boolean isLastNode = true;

    protected final Rect appendNodeRectRight = new Rect();
    protected final Rect appendNodeRectDown = new Rect();
    protected final Rect deleteSubTreeRect = new Rect();
    protected final Rect nodeItemRect = new Rect();
    protected final Rect chainClipRect = new Rect();

    protected float renderAlpha = 1.0f;
    private final boolean playEntranceAnimation;
    private float entranceAlpha;
    protected float revealProgress = 1.0f;
    protected boolean collapseAnimating = false;

    private static final TooltipContent deleteSubTreeTip = TooltipContent.of(
            Component.translatable("gui.paramagic.spell_edit_table.para_tree.tooltip.remove_sub_path")
                    .withStyle(ChatFormatting.GRAY)
    );

    private static final TooltipContent appendNodeRightTip = TooltipContent.of(
            Component.translatable("gui.paramagic.spell_edit_table.para_tree.tooltip.add_node")
                    .withStyle(ChatFormatting.GREEN),
            Component.translatable("gui.paramagic.spell_edit_table.para_tree.tooltip.remove_node")
                    .withStyle(ChatFormatting.GRAY)
    );

    private static final TooltipContent appendNodeDownTip = TooltipContent.of(
            Component.translatable("gui.paramagic.spell_edit_table.para_tree.tooltip.add_branch")
                    .withStyle(ChatFormatting.GREEN)
    );

    public TreeNode() {
        this(null, null, false);
    }

    public TreeNode(@Nullable String nodeId, @Nullable ParaOpId operatorId) {
        this(nodeId, operatorId, false);
    }

    public TreeNode(@Nullable String nodeId, @Nullable ParaOpId operatorId, boolean playEntranceAnimation) {
        super();
        this.nodeId = nodeId;
        this.operatorId = operatorId;
        this.playEntranceAnimation = playEntranceAnimation;
        this.entranceAlpha = playEntranceAnimation ? 0.0f : 1.0f;
        ParaOperator operator = operatorId == null ? null : AllParaOperators.createOperator(operatorId);
        this.renderingItem = operator == null ? ItemStack.EMPTY : operator.getRenderStack().copy();
        this.state = SubTreeState.ADDABLE;
        this.hiddenSubTreeNode = new HiddenSubTreeNode(this);
        this.hiddenSubTreeNode.disable();
        this.addChild(this.hiddenSubTreeNode);
    }

    @Override
    @Nullable
    public TooltipContent getTooltip(@Nonnull TooltipQuery query) {
        final float x = query.mouseX();
        final float y = query.mouseY();
        if (this.nodeItemRect.contains(x, y)) {
            return UINode.getTooltipFromItem(this.renderingItem);
        }
        if (this.deleteSubTreeRect.contains(x, y) && this.state == SubTreeState.EXPANDED) {
            return deleteSubTreeTip;
        }
        if (this.appendNodeRectRight.contains(x,y)) {
            return appendNodeRightTip;
        }
        if (this.appendNodeRectDown.contains(x, y)) {
            return appendNodeDownTip;
        }
        return null;
    }

    @Override
    protected void onAttached(@NotNull UIManager manager) {
        if (!this.playEntranceAnimation) {
            return;
        }
        this.animateFloat(
                0.0f,
                1.0f,
                0.4f,
                EasingFunction.easeInOutQuad,
                Interpolation::linear,
                value -> this.entranceAlpha = value
        ).setDelay(0.3f);
    }

    /**
     * The single alpha value for every non-item visual belonging to this node.
     * It combines the initial entrance animation with the expand/collapse reveal state.
     */
    protected final float getEffectiveRenderAlpha() {
        return this.renderAlpha * this.entranceAlpha;
    }

    @Nullable
    public String getNodeId() { return this.nodeId; }

    public int getChildCount() { return this.subNode.size(); }

    protected boolean canHitAppendNodeRight(float mouseX, float mouseY) {
        return this.isLastNode() && this.appendNodeRectRight.contains(mouseX, mouseY);
    }

    protected boolean canHitAppendNodeDown(float mouseX, float mouseY) {
        return this.state == SubTreeState.ADDABLE && this.appendNodeRectDown.contains(mouseX, mouseY);
    }

    protected boolean canHitDeleteNode(float mouseX, float mouseY) {
        return UIManager.hasCtrlKeyDown() && this.canHitAppendNodeRight(mouseX, mouseY);
    }

    protected boolean canHitDeleteSubTree(float mouseX, float mouseY) {
        if (!UIManager.hasCtrlKeyDown()) {
            return false;
        }
        return !this.collapseAnimating && this.state == SubTreeState.EXPANDED && this.deleteSubTreeRect.contains(mouseX, mouseY);
    }

    protected boolean canHitInteractNode(float mouseX, float mouseY) {
        return this.nodeItemRect.contains(mouseX, mouseY);
    }

    protected void createSubTree(TreeNode subNode) {
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

    public boolean deleteSubTree() {
        if (this.state != SubTreeState.EXPANDED || this.subNode.isEmpty()) {
            return false;
        }

        this.onSubTreeDeleting();
        for (TreeNode child : List.copyOf(this.subNode)) {
            this.removeChild(child);
        }
        this.subNode.clear();
        this.expandedSubNode = null;
        this.hiddenSubTreeNode.disable();
        this.setSubTreeAddable();
        this.requestMeasure();
        return true;
    }

    public boolean deleteFromParent() {
        if (!this.isLastNode() || !(this.getParent() instanceof TreeNode parentNode)) {
            return false;
        }

        if (!parentNode.subNode.remove(this)) {
            return false;
        }
        this.onNodeDeleting();
        parentNode.removeChild(this);

        if (parentNode.expandedSubNode == this) {
            parentNode.expandedSubNode = null;
        }
        if (!parentNode.subNode.isEmpty()) {
            parentNode.subNode.get(parentNode.subNode.size() - 1).setLastNode(true);
        }
        parentNode.requestMeasure();
        return true;
    }

    protected void applyExpandedPath(@Nonnull List<TreeNode> expandedPath, int pathIndex) {
        if (pathIndex < 0 || pathIndex >= expandedPath.size() || expandedPath.get(pathIndex) != this) {
            return;
        }

        this.hiddenSubTreeNode.disable();
        if (this.subNode.isEmpty()) {
            this.expandedSubNode = null;
            this.setSubTreeAddable();
            return;
        }

        TreeNode nextExpandedNode = null;
        if (pathIndex + 1 < expandedPath.size()) {
            nextExpandedNode = expandedPath.get(pathIndex + 1);
        }

        this.expandedSubNode = nextExpandedNode;
        this.setSubTreeExpanded();

        for (TreeNode child : this.subNode) {
            child.enableWithoutLayout();
            if (child == nextExpandedNode) {
                child.applyExpandedPath(expandedPath, pathIndex + 1);
            } else {
                child.collapseForSingleExpandedPath();
            }
        }
    }

    /**
     * Restores a previously visible path while rebuilding tree data. This is deliberately
     * separate from {@link #applyExpandedPath(List, int)}: restoration is not a user-initiated
     * expansion and therefore must not replay the expand animation.
     */
    protected void restoreExpandedPathImmediately(@Nonnull List<TreeNode> expandedPath, int pathIndex) {
        if (pathIndex < 0 || pathIndex >= expandedPath.size() || expandedPath.get(pathIndex) != this) {
            return;
        }

        this.hiddenSubTreeNode.disable();
        this.collapseAnimating = false;
        this.revealProgress = 1.0f;
        this.renderAlpha = 1.0f;
        if (this.subNode.isEmpty()) {
            this.expandedSubNode = null;
            this.setSubTreeAddable();
            return;
        }

        TreeNode nextExpandedNode = pathIndex + 1 < expandedPath.size()
                ? expandedPath.get(pathIndex + 1)
                : null;
        this.expandedSubNode = nextExpandedNode;
        this.state = SubTreeState.EXPANDED;

        for (TreeNode child : this.subNode) {
            child.enableWithoutLayout();
            if (child == nextExpandedNode) {
                child.restoreExpandedPathImmediately(expandedPath, pathIndex + 1);
            } else {
                child.collapseForSingleExpandedPathImmediately();
            }
        }
    }

    protected void collapseForSingleExpandedPath() {
        this.expandedSubNode = null;

        if (this.subNode.isEmpty()) {
            this.setSubTreeAddable();
            this.hiddenSubTreeNode.disable();
            return;
        }

        this.setSubTreeHidden();
        if (this.collapseAnimating) {
            return;
        }

        this.finishChildNodesForHiddenState();
    }

    protected void setSubTreeExpanded() {
        boolean shouldAnimate = this.state != SubTreeState.EXPANDED || this.collapseAnimating || this.revealProgress < 1.0f;
        if (shouldAnimate) {
            if (this.state != SubTreeState.EXPANDED && !this.collapseAnimating) {
                this.setRevealProgress(0.0f);
            }
            UIManager m = this.getManager();
            if (m != null) {
                m.offerDeferredTask(new UITask(manager -> this.onSubTreeExpanding(), TaskStage.BEFORE_RENDER));
            } else {
                this.revealProgress = 1.0f;
                this.renderAlpha = 1.0f;
            }
        }
        this.collapseAnimating = false;
        this.hiddenSubTreeNode.disable();
        this.state = SubTreeState.EXPANDED;
        this.requestMeasure();
    }

    protected void setSubTreeHidden() {
        if (this.state == SubTreeState.EXPANDED) {
            this.onSubTreeCollapsing();
            this.state = SubTreeState.HIDDEN;
            this.requestMeasure();
            return;
        }
        this.state = SubTreeState.HIDDEN;
        this.revealProgress = 0.0f;
        this.renderAlpha = MIN_REVEAL_ALPHA;
        this.requestMeasure();
    }

    protected void setSubTreeAddable() {
        this.collapseAnimating = false;
        this.revealProgress = 1.0f;
        this.renderAlpha = 1.0f;
        this.state = SubTreeState.ADDABLE;
    }

    protected void onSubTreeExpanding() {
        this.collapseAnimating = false;
        this.hiddenSubTreeNode.disable();
        this.animateFloat(
                REVEAL_ANIMATION_KEY,
                this.revealProgress,
                1.0f,
                0.3f,
                EasingFunction.easeOutSine,
                Interpolation::linear,
                this::setRevealProgress
        ).setOnComplete(() -> this.setRevealProgress(1.0f));
    }

    protected void onSubTreeCollapsing() {
        UIManager manager = this.getManager();
        if (manager == null) {
            this.finishCollapseToHidden();
            return;
        }

        this.collapseAnimating = true;
        this.hiddenSubTreeNode.disable();
        this.animateFloat(
                REVEAL_ANIMATION_KEY,
                this.revealProgress,
                0.0f,
                0.3f,
                EasingFunction.easeOutSine,
                Interpolation::linear,
                this::setRevealProgress
        ).setOnComplete(this::finishCollapseToHidden);
    }

    protected void onSubTreeDeleting() {
    }

    protected void onNodeDeleting() {
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

    private void finishCollapseToHidden() {
        this.collapseAnimating = false;
        this.setRevealProgress(0.0f);
        this.state = SubTreeState.HIDDEN;
        this.finishChildNodesForHiddenState();
        this.requestMeasure();
    }

    private void setRevealProgress(float revealProgress) {
        this.revealProgress = Math.max(0.0f, Math.min(1.0f, revealProgress));
        this.renderAlpha = this.getRevealAlpha();
    }

    private float getRevealAlpha() {
        if (this.state == SubTreeState.HIDDEN && !this.collapseAnimating) {
            return 1.0f;
        }
        return MIN_REVEAL_ALPHA + (1.0f - MIN_REVEAL_ALPHA) * this.revealProgress;
    }

    private void finishChildNodesForHiddenState() {
        this.hiddenSubTreeNode.enable();
        for (TreeNode child : this.subNode) {
            child.collapseForSingleExpandedPathImmediately();
            child.disableWithoutLayout();
        }
    }

    private void collapseForSingleExpandedPathImmediately() {
        UIManager manager = this.getManager();
        if (manager != null) {
            manager.removeAnimator(manager.getAnimator(this, REVEAL_ANIMATION_KEY));
        }

        this.expandedSubNode = null;
        this.collapseAnimating = false;

        if (this.subNode.isEmpty()) {
            this.setRevealProgress(1.0f);
            this.setSubTreeAddable();
            this.hiddenSubTreeNode.disable();
            return;
        }

        this.setRevealProgress(0.0f);
        this.state = SubTreeState.HIDDEN;
        this.hiddenSubTreeNode.enable();

        for (TreeNode child : this.subNode) {
            child.collapseForSingleExpandedPathImmediately();
            child.disableWithoutLayout();
        }
    }

    @Override
    public MeasureResult measure(@Nonnull LayoutConstraints constraints) {
        LayoutConstraints childConstraints = LayoutConstraints.loose(constraints.getMaxWidth(), constraints.getMaxHeight());
        this.hiddenSubTreeNode.measure(childConstraints);
        for (TreeNode treeNode : this.subNode) {
            treeNode.measure(childConstraints);
        }

        MeasureResult result = this.measureTreeNode();
        this.setMeasureResult(result);
        this.measureDirty = false;
        return result;
    }

    @Nonnull
    protected MeasureResult measureTreeNode() {
        float w;
        float h;
        switch (this.state) {
            case HIDDEN -> {
                w = Math.max(this.getOwnNodeWidth(), this.hiddenSubTreeNode.getMeasuredWidth());
                h = this.getHiddenChildOffsetY() + this.hiddenSubTreeNode.getMeasuredHeight();
            }
            case EXPANDED -> {
                float childrenWidth = this.getVisibleChildrenExtentWidth();
                float childrenHeight = this.getVisibleChildrenMaxHeight();
                w = Math.max(this.getOwnNodeWidth(), childrenWidth);
                h = this.getExpandedChildOffsetY() + childrenHeight;
            }
            case ADDABLE -> {
                w = Math.max(this.getOwnNodeWidth(), WEAssets.ADD_NODE_DOWN.width);
                h = this.getAddableChildOffsetY() + WEAssets.ADD_NODE_DOWN.height;
            }
            default -> {
                w = this.getOwnNodeWidth();
                h = WEAssets.ITEM_RECT.height;
            }
        }
        return MeasureResult.of(w, h);
    }

    protected float getOwnNodeWidth() {
        float w = WEAssets.ITEM_RECT.width;
        if (this.isLastNode()) {
            w += WEAssets.ADD_NODE_RIGHT.width + ADD_NODE_GAP;
        }
        return w;
    }

    protected float getVisibleChildrenExtentWidth() {
        if (this.subNode.isEmpty()) {
            return 0.0f;
        }

        float offsetX = 0.0f;
        float maxRight = 0.0f;
        for (TreeNode treeNode : this.subNode) {
            maxRight = Math.max(maxRight, offsetX + treeNode.getMeasuredWidth());
            offsetX += treeNode.getOwnNodeWidth() + ITEM_RECT_GAP;
        }
        return maxRight;
    }

    protected float getVisibleChildrenMaxHeight() {
        float height = 0.0f;
        for (TreeNode treeNode : this.subNode) {
            height = Math.max(height, treeNode.getMeasuredHeight());
        }
        return height;
    }

    protected float getHiddenChildOffsetY() {
        return WEAssets.ITEM_RECT.height + CHAIN_Y_GAP * 2.0f + WEAssets.CHAIN.height;
    }

    protected float getExpandedChildOffsetY() {
        return WEAssets.ITEM_RECT.height + CHAIN_Y_GAP * 2.0f + WEAssets.CHAIN.height * 3.0f + BETWEEN_NODE_GAP * 2.0f;
    }

    protected float getAddableChildOffsetY() {
        return WEAssets.ITEM_RECT.height + CHAIN_Y_GAP;
    }

    @Override
    protected void arrangeSelf(float parentX, float parentY, float parentW, float parentH) {
        super.arrangeSelf(parentX, parentY, parentW, parentH);
        this.nodeItemRect.set(
                this.finalRect.x,
                this.finalRect.y,
                WEAssets.ITEM_RECT.width,
                WEAssets.ITEM_RECT.height
        );
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
        this.deleteSubTreeRect.set(0.0f, 0.0f, 0.0f, 0.0f);
        switch (this.state) {
            case HIDDEN -> this.appendNodeRectDown.set(0.0f, 0.0f, 0.0f, 0.0f);
            case EXPANDED -> {
                this.appendNodeRectDown.set(0.0f, 0.0f, 0.0f, 0.0f);
                this.deleteSubTreeRect.set(
                        this.finalRect.x + (WEAssets.ITEM_RECT.width - WEAssets.CHAIN.width) / 2.0f,
                        this.finalRect.y + WEAssets.ITEM_RECT.height + CHAIN_Y_GAP,
                        WEAssets.CHAIN.width,
                        WEAssets.CHAIN.height * 3 + BETWEEN_NODE_GAP * 2
                );
            }
            case ADDABLE -> this.appendNodeRectDown.set(
                    this.finalRect.x + (WEAssets.ITEM_RECT.width - WEAssets.ADD_NODE_DOWN.width) / 2.0f,
                    this.finalRect.y + WEAssets.ITEM_RECT.height + ADD_NODE_GAP,
                    WEAssets.ADD_NODE_DOWN.width,
                    WEAssets.ADD_NODE_DOWN.height
            );
        }
    }

    /**
     * A tree node's layout rectangle also contains its expanded descendants. Only the actual
     * item cell and explicit controls may become an event target; descendants are visited first.
     */
    @Override
    public boolean hitTest(float mouseX, float mouseY) {
        if (!this.visible || !this.hitTestable) {
            return false;
        }
        return this.nodeItemRect.contains(mouseX, mouseY)
                || this.appendNodeRectRight.contains(mouseX, mouseY)
                || this.appendNodeRectDown.contains(mouseX, mouseY)
                || this.deleteSubTreeRect.contains(mouseX, mouseY);
    }

    @Override
    protected void arrangeChildren() {
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        if (this.collapseAnimating) {
            offsetY = this.getExpandedChildOffsetY();
        } else {
            switch (this.state) {
                case HIDDEN -> offsetY = this.getHiddenChildOffsetY();
                case EXPANDED -> offsetY = this.getExpandedChildOffsetY();
                case ADDABLE -> offsetY = this.getAddableChildOffsetY() + WEAssets.ADD_NODE_DOWN.height;
            }
        }

        Rect rect = this.hiddenSubTreeNode.getLayoutRect();
        rect.set(offsetX, offsetY, this.hiddenSubTreeNode.getMeasuredWidth(), this.hiddenSubTreeNode.getMeasuredHeight());
        this.hiddenSubTreeNode.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);

        for (TreeNode treeNode : this.subNode) {
            Rect layoutRect = treeNode.getLayoutRect();
            layoutRect.set(offsetX, offsetY, treeNode.getOwnNodeWidth(), treeNode.getMeasuredHeight());
            treeNode.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
            offsetX += treeNode.getOwnNodeWidth() + ITEM_RECT_GAP;
        }
    }

    @Override
    protected void renderChildrenTree(UIRenderContext context) {
        this.updateChildPresentationForReveal();
        super.renderChildrenTree(context);
    }

    private void updateChildPresentationForReveal() {
        if (this.state != SubTreeState.EXPANDED && !this.collapseAnimating) {
            return;
        }

        float sourceX = this.presentationRect.x;
        float sourceY = this.presentationRect.y + WEAssets.ITEM_RECT.height + CHAIN_Y_GAP;
        float inheritedOffsetX = this.presentationRect.x - this.finalRect.x;
        float inheritedOffsetY = this.presentationRect.y - this.finalRect.y;
        float alpha = Math.max(0.0f, Math.min(1.0f, this.revealProgress));

        for (TreeNode child : this.subNode) {
            float targetX = child.finalRect.x + inheritedOffsetX;
            float targetY = child.finalRect.y + inheritedOffsetY;
            float x = sourceX + (targetX - sourceX) * alpha;
            float y = sourceY + (targetY - sourceY) * alpha;
            child.applyPresentationStateToSubtree(x - child.finalRect.x, y - child.finalRect.y, this.renderAlpha);
        }
    }

    private void applyPresentationStateToSubtree(float offsetX, float offsetY, float inheritedAlpha) {
        float alpha = inheritedAlpha * this.getRevealAlpha();
        this.renderAlpha = alpha;
        this.presentationRect.set(
                this.finalRect.x + offsetX,
                this.finalRect.y + offsetY,
                this.finalRect.w,
                this.finalRect.h
        );
        for (UINode child : this.getChildren()) {
            child.presentationRect.set(
                    child.finalRect.x + offsetX,
                    child.finalRect.y + offsetY,
                    child.finalRect.w,
                    child.finalRect.h
            );
            if (child instanceof TreeNode treeNode) {
                treeNode.applyPresentationStateToSubtree(offsetX, offsetY, alpha);
            }
        }
    }

    @Override
    protected void render(@NotNull UIRenderContext context) {
        float x = this.presentationRect.x;
        float y = this.presentationRect.y;
        float alpha = this.getEffectiveRenderAlpha();
        context.renderSpriteWithAlpha(WEAssets.ITEM_RECT, x, y, alpha);
        if (!this.renderingItem.isEmpty() && alpha >= ITEM_VISIBILITY_ALPHA_THRESHOLD) {
            final float xOffset = 3.0f;
            final float yOffset = 3.0f;
            context.renderItem(this.renderingItem, (int) (x + xOffset), (int) (y + yOffset));
            context.renderItemDecorations(this.renderingItem, (int) (x + xOffset), (int) (y + yOffset));
        }
        y += WEAssets.ITEM_RECT.height + CHAIN_Y_GAP;
        switch (this.state) {
            case EXPANDED -> {
                x += (WEAssets.ITEM_RECT.width - WEAssets.CHAIN.width) / 2.0f;
                this.renderExpandedChain(context, x, y, alpha);
            }
            case HIDDEN -> {
                x += (WEAssets.ITEM_RECT.width - WEAssets.CHAIN.width) / 2.0f;
                if (this.collapseAnimating) {
                    this.renderExpandedChain(context, x, y, alpha);
                } else {
                    context.renderSpriteWithAlpha(WEAssets.CHAIN, x, y, alpha);
                }
            }
            case ADDABLE -> {
                x += (WEAssets.ITEM_RECT.width - WEAssets.ADD_NODE_DOWN.width) / 2.0f;
                context.renderSpriteWithAlpha(WEAssets.ADD_NODE_DOWN, x, y, alpha);
            }
        }
        if (this.isLastNode()) {
            context.renderSpriteWithAlpha(
                    WEAssets.ADD_NODE_RIGHT,
                    this.presentationRect.x + WEAssets.ITEM_RECT.width + ADD_NODE_GAP,
                    this.presentationRect.y + (WEAssets.ITEM_RECT.height - WEAssets.ADD_NODE_RIGHT.height) / 2.0f,
                    alpha
            );
        }
    }

    private void renderExpandedChain(@Nonnull UIRenderContext context, float x, float y, float alpha) {
        float visibleHeight = (
                WEAssets.CHAIN.height * 3.0f + BETWEEN_NODE_GAP * 2.0f
        ) * Math.max(0.0f, Math.min(1.0f, this.revealProgress));
        if (visibleHeight <= 0.0f) {
            return;
        }

        this.chainClipRect.set(x, y, WEAssets.CHAIN.width, visibleHeight);
        context.pushClipRect(this.chainClipRect);
        for (int i = 0; i < 3; i++) {
            context.renderSpriteWithAlpha(WEAssets.CHAIN, x, y, alpha);
            y += WEAssets.CHAIN.height + BETWEEN_NODE_GAP;
        }
        context.popClipRect();
    }

    @Override
    public void renderDebug(@NotNull UIRenderContext context) {
        super.renderDebug(context);
        context.renderOutline(this.appendNodeRectRight, UIColor.BLUE);
        context.renderOutline(this.appendNodeRectDown, UIColor.BLUE);
        context.renderOutline(this.deleteSubTreeRect, UIColor.GREEN);
    }

    @Override
    protected void onMouseRelease(UIEventContext<MouseRelease> context) {
        context.consume();
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
