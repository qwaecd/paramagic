package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.thaumaturgy.spelltree.SpellTreeEditTarget;
import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.animation.UIAnimator;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.*;
import com.qwaecd.paramagic.ui.event.EventPhase;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseRelease;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;
import com.qwaecd.paramagic.ui.util.Rect;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import javax.annotation.Nonnull;

public final class TreeScrollViewport extends UINode {
    private final TreeContent treeContent;
    private final Rect clipRect = new Rect();

    private float scrollX = 0.0f;
    private float scrollY = 0.0f;

    private static final float SCROLL_STRENGTH = 20.5f;
    private final Vector2f scrollVelocity = new Vector2f(0.0f, 0.0f);

    private boolean captured = false;

    public TreeScrollViewport(SpellTreeEditTarget editTarget) {
        super();
        this.treeContent = new TreeContent(editTarget);
        this.clipMod = ClipMod.RECT;
        this.addChild(this.treeContent);
        this.addListener(AllUIEvents.MOUSE_CLICK, EventPhase.BUBBLING, this::onMouseClick);
        this.addListener(AllUIEvents.MOUSE_RELEASE, EventPhase.BUBBLING, this::onMouseRelease);
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
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.requestLayout();
    }

    void setScrollWithAnim(float scrollX, float scrollY, float duration) {
        UIManager manager = this.getManager();
        if (manager == null) {
            return;
        }
        manager.removeAnimator(manager.getAnimator(this, "TreeScroll"));
        UIAnimator<Vector2f> animator = new UIAnimator<>(
                new Vector2f(this.scrollX, this.scrollY),
                new Vector2f(scrollX, scrollY),
                duration,
                EasingFunction.easeOutSine,
                Interpolation::linear,
                v -> this.setScroll(v.x, v.y)
        );
        this.animate("TreeScroll", animator);
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        if (context.isConsumed()) {
            return;
        }
        UIManager manager = this.getManager();
        if (manager == null || this.captured) {
            return;
        }
        this.scrollVelocity.set(0.0f);
        manager.removeAnimator(manager.getAnimator(this, "TreeScroll"));
        manager.captureNode(this);
        this.captured = true;
        context.consume();
    }

    @Override
    protected void onMouseRelease(UIEventContext<MouseRelease> context) {
        if (context.isConsumed()) {
            return;
        }
        UIManager manager = this.getManager();
        if (manager == null || !this.captured) {
            return;
        }
        manager.releaseCapture();
        this.captured = false;
        float targetX = this.scrollX + this.scrollVelocity.x * SCROLL_STRENGTH;
        float targetY = this.scrollY + this.scrollVelocity.y * SCROLL_STRENGTH;
        float minScrollX = -55.0f;
        float minScrollY = -45.0f;
        float maxScrollX = this.treeContent.getMeasuredWidth() + (-minScrollX) - this.finalRect.w;
        float maxScrollY = this.treeContent.getMeasuredHeight() + (-minScrollY) - this.finalRect.h;
        targetX = Math.max(minScrollX, Math.min(maxScrollX, targetX));
        targetY = Math.max(minScrollY, Math.min(maxScrollY, targetY));
//        if (targetX < minScrollX) {
//            targetX = minScrollX;
//        } else if (targetX > maxScrollX) {
//            targetX = maxScrollX;
//        }
//        if (targetY < minScrollY) {
//            targetY = minScrollY;
//        } else if (targetY > maxScrollY) {
//            targetY = maxScrollY;
//        }
        this.setScrollWithAnim(targetX, targetY, 0.5f);
        context.consume();
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY, MouseStateMachine mouseState) {
        this.scrollVelocity.set((float) -mouseState.deltaX(), (float) -mouseState.deltaY());
        this.setScroll((float) (this.scrollX - mouseState.deltaX()), (float) (this.scrollY - mouseState.deltaY()));
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
//        float maxScrollX = Math.max(0.0f, this.treeContent.getMeasuredWidth() - this.finalRect.w);
//        float maxScrollY = Math.max(0.0f, this.treeContent.getMeasuredHeight() - this.finalRect.h);
//        this.scrollX = Math.min(this.scrollX, maxScrollX);
//        this.scrollY = Math.min(this.scrollY, maxScrollY);

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

    @Override
    protected void render(@NotNull UIRenderContext context) {
    }
}
