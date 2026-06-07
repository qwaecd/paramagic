package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import com.qwaecd.paramagic.ui_project.wand.SpellTreeEditClientState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public final class TreeCanvas extends UINode {
    private static final float PANEL_PADDING = 10.0f;
    private static final float SCROLLBAR_THICKNESS = 7.0f;

    private float offsetAlpha = 0.6f;
    private float renderAlpha = 0.1f;

    private final TreeContent treeContent;
    private final TreeScrollViewport viewport;
    private final TreeScrollbarLayer scrollbarLayer;

    private final Rect topScrollbarRect = new Rect();
    private final Rect rightScrollbarRect = new Rect();

    public TreeCanvas(@Nonnull SpellTreeEditClientState editState) {
        super();
        this.viewport = new TreeScrollViewport(editState);
        this.scrollbarLayer = new TreeScrollbarLayer();

        this.treeContent = this.viewport.getTreeContent();
        this.addChild(this.scrollbarLayer);
        this.addChild(this.viewport);
    }

    public void onTreeDataRebuilt() {
        this.treeContent.onTreeDataRebuilt();
    }

    @Override
    protected void onAttached(@Nonnull UIManager manager) {
        this.animateFloat(
                this.offsetAlpha,
                1.0f,
                0.4f,
                EasingFunction.easeOutSine,
                Interpolation::linear,
                (v -> this.offsetAlpha = v)
        );
        this.animateFloat(
                this.renderAlpha,
                1.0f,
                0.4f,
                EasingFunction.easeInOutQuad,
                Interpolation::linear,
                (v -> this.renderAlpha = v)
        );
    }

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        return MeasureResult.of(260.0f, 210.0f);
    }

    @Override
    protected void arrangeChildren() {
        final float scrollbarLayerX = PANEL_PADDING;
        final float scrollbarLayerY = PANEL_PADDING;
        final float scrollbarLayerW = Math.max(0.0f, this.finalRect.w - PANEL_PADDING * 2.0f);
        final float scrollbarLayerH = Math.max(0.0f, this.finalRect.h - PANEL_PADDING * 2.0f);

        float viewportWidth = Math.max(0.0f, scrollbarLayerW - SCROLLBAR_THICKNESS);
        float viewportHeight = Math.max(0.0f, scrollbarLayerH - SCROLLBAR_THICKNESS);
        this.topScrollbarRect.set(
                0.0f,
                0.0f,
                viewportWidth,
                SCROLLBAR_THICKNESS
        );
        this.rightScrollbarRect.set(
                viewportWidth,
                SCROLLBAR_THICKNESS,
                SCROLLBAR_THICKNESS,
                viewportHeight
        );

        this.scrollbarLayer.updateTrackRectsForLayout(this.topScrollbarRect, this.rightScrollbarRect);
        this.scrollbarLayer.updateMetricsForLayout(
                viewportWidth,
                viewportHeight,
                this.viewport.getContentWidth(),
                this.viewport.getContentHeight(),
                this.viewport.getScrollX(),
                this.viewport.getScrollY()
        );
        this.scrollbarLayer.getLayoutRect().set(scrollbarLayerX, scrollbarLayerY, scrollbarLayerW, scrollbarLayerH);
        this.scrollbarLayer.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);

        final float viewportX = PANEL_PADDING;
        final float viewportY = PANEL_PADDING + SCROLLBAR_THICKNESS;
        Rect viewportRect = this.viewport.getLayoutRect();
        viewportRect.set(viewportX, viewportY, viewportWidth, viewportHeight);
        this.viewport.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
    }

    @Override
    protected void render(@NotNull UIRenderContext context) {
    }

    @Override
    protected void renderBackGround(@Nonnull UIRenderContext context) {
        float x = this.finalRect.x + this.finalRect.w / 2.0f * (1.0f - this.offsetAlpha);
        float y = this.finalRect.y + this.finalRect.h / 2.0f * (1.0f - this.offsetAlpha);
        context.renderNineSliceSpriteWithAlpha(
                WEAssets.SPELL_EDIT_RECT,
                (int) x,
                (int) y,
                (int) (this.finalRect.w * this.offsetAlpha),
                (int) (this.finalRect.h * this.offsetAlpha),
                this.renderAlpha
        );

        if (this.isDebugMod()) {
            this.renderDebug(context);
        }
    }
}
