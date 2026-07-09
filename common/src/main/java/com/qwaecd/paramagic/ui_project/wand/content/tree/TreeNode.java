package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.*;
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
    private static final String REVEAL_ANIMATION_KEY = "TreeNodeReveal";
    private static final float MIN_REVEAL_ALPHA = 0.1f;

    @Nonnull
    protected SubTreeState state;

    @Nullable
    protected TreeNode expandedSubNode = null;

    protected final HiddenSubTreeNode hiddenSubTreeNode;

    protected boolean isLastNode = true;

    protected final Rect appendNodeRectRight = new Rect();
    protected final Rect appendNodeRectDown = new Rect();
    protected final Rect deleteSubTreeRect = new Rect();
    protected final Rect chainClipRect = new Rect();

    protected float renderAlpha = 1.0f;
    protected float revealProgress = 1.0f;
    protected boolean collapseAnimating = false;

    public TreeNode() {
        super();
        this.state = SubTreeState.ADDABLE;
        this.hiddenSubTreeNode = new HiddenSubTreeNode(this);
        this.hiddenSubTreeNode.disable();
        this.addChild(this.hiddenSubTreeNode);
    }

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
            if (this.state != SubTreeState.EXPANDED) {
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
    }

    protected void setSubTreeHidden() {
        if (this.state == SubTreeState.EXPANDED) {
            this.onSubTreeCollapsing();
            if (this.collapseAnimating) {
                return;
            }
        }
        this.state = SubTreeState.HIDDEN;
        this.revealProgress = 0.0f;
        this.renderAlpha = MIN_REVEAL_ALPHA;
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
        context.renderSprite(WEAssets.ITEM_RECT, x, y);
        y += WEAssets.ITEM_RECT.height + CHAIN_Y_GAP;
        switch (this.state) {
            case EXPANDED -> {
                x += (WEAssets.ITEM_RECT.width - WEAssets.CHAIN.width) / 2.0f;
                this.renderExpandedChain(context, x, y);
            }
            case HIDDEN -> {
                x += (WEAssets.ITEM_RECT.width - WEAssets.CHAIN.width) / 2.0f;
                context.renderSprite(WEAssets.CHAIN, x, y);
            }
            case ADDABLE -> {
                x += (WEAssets.ITEM_RECT.width - WEAssets.ADD_NODE_DOWN.width) / 2.0f;
                context.renderSpriteWithAlpha(WEAssets.ADD_NODE_DOWN, x, y, this.renderAlpha);
            }
        }
        if (this.isLastNode()) {
            context.renderSprite(
                    WEAssets.ADD_NODE_RIGHT,
                    this.presentationRect.x + WEAssets.ITEM_RECT.width + ADD_NODE_GAP,
                    this.presentationRect.y + (WEAssets.ITEM_RECT.height - WEAssets.ADD_NODE_RIGHT.height) / 2.0f
            );
        }
    }

    private void renderExpandedChain(@Nonnull UIRenderContext context, float x, float y) {
        float visibleHeight = (
                WEAssets.CHAIN.height * 3.0f + BETWEEN_NODE_GAP * 2.0f
        ) * Math.max(0.0f, Math.min(1.0f, this.revealProgress));
        if (visibleHeight <= 0.0f) {
            return;
        }

        this.chainClipRect.set(x, y, WEAssets.CHAIN.width, visibleHeight);
        context.pushClipRect(this.chainClipRect);
        for (int i = 0; i < 3; i++) {
            context.renderSprite(WEAssets.CHAIN, x, y);
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
