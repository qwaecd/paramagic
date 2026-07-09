package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public final class TreeScrollbarLayer extends UINode {
    private final TreeScrollbarThumb horizontalThumb =
            new TreeScrollbarThumb(TreeScrollbarThumb.Orientation.HORIZONTAL);
    private final TreeScrollbarThumb verticalThumb =
            new TreeScrollbarThumb(TreeScrollbarThumb.Orientation.VERTICAL);

    private final Rect topTrackRect = new Rect();
    private final Rect rightTrackRect = new Rect();

    private float viewportWidth;
    private float viewportHeight;
    private float contentWidth;
    private float contentHeight;
    private float scrollX;
    private float scrollY;

    private float offsetAlpha = 0.0f;
    private float renderAlpha = 0.0f;

    public TreeScrollbarLayer() {
        super();
        this.addChild(this.horizontalThumb);
        this.addChild(this.verticalThumb);
    }

    @Override
    protected void onAttached(@NotNull UIManager manager) {
        this.animateFloat(
                this.offsetAlpha,
                1.0f,
                0.8f,
                EasingFunction.easeOutCubic,
                Interpolation::linear,
                (v -> this.offsetAlpha = v)
        ).setDelay(0.35f);
        this.animateFloat(
                this.renderAlpha,
                1.0f,
                0.5f,
                EasingFunction.easeInOutQuad,
                Interpolation::linear,
                (v -> {
                    this.renderAlpha = v;
                    this.horizontalThumb.setRenderAlpha(v);
                    this.verticalThumb.setRenderAlpha(v);
                })
        ).setDelay(0.35f);
        this.horizontalThumb.setRenderAlpha(this.renderAlpha);
        this.verticalThumb.setRenderAlpha(this.renderAlpha);
    }

    public void updateMetricsForLayout(
            float viewportWidth,
            float viewportHeight,
            float contentWidth,
            float contentHeight,
            float scrollX,
            float scrollY
    ) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.contentWidth = contentWidth;
        this.contentHeight = contentHeight;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
    }

    public void updateTrackRectsForLayout(@Nonnull Rect topTrackRect, @Nonnull Rect rightTrackRect) {
        this.topTrackRect.set(topTrackRect.x, topTrackRect.y, topTrackRect.w, topTrackRect.h);
        this.rightTrackRect.set(rightTrackRect.x, rightTrackRect.y, rightTrackRect.w, rightTrackRect.h);
    }

    @Override
    protected void arrangeChildren() {
        Rect horizontalRect = this.horizontalThumb.getLayoutRect();
        this.layoutThumb(
                horizontalRect,
                this.topTrackRect,
                this.viewportWidth,
                this.contentWidth,
                this.scrollX,
                true
        );
        this.horizontalThumb.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);

        Rect verticalRect = this.verticalThumb.getLayoutRect();
        this.layoutThumb(
                verticalRect,
                this.rightTrackRect,
                this.viewportHeight,
                this.contentHeight,
                this.scrollY,
                false
        );
        this.verticalThumb.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
    }

    private void layoutThumb(
            @Nonnull Rect thumbRect,
            @Nonnull Rect trackRect,
            float viewportSize,
            float contentSize,
            float scrollOffset,
            boolean horizontal
    ) {
        float trackSize = horizontal ? trackRect.w : trackRect.h;
        float crossSize = horizontal ? trackRect.h : trackRect.w;
        float thumbSize = horizontal ? WEAssets.SLIDER_HOR.width : WEAssets.SLIDER_VER.height;
        float thumbCrossSize = horizontal ? WEAssets.SLIDER_HOR.height : WEAssets.SLIDER_VER.width;
        float thumbOffset = this.computeThumbOffset(trackSize, thumbSize, viewportSize, contentSize, scrollOffset);
        float crossOffset = Math.max(0.0f, (crossSize - thumbCrossSize) / 2.0f);

        if (horizontal) {
            thumbRect.set(trackRect.x + thumbOffset, trackRect.y + crossOffset, thumbSize, thumbCrossSize);
            return;
        }
        thumbRect.set(trackRect.x + crossOffset, trackRect.y + thumbOffset, thumbCrossSize, thumbSize);
    }

    private float computeThumbOffset(
            float trackSize,
            float thumbSize,
            float viewportSize,
            float contentSize,
            float scrollOffset
    ) {
        float scrollable = Math.max(0.0f, contentSize - viewportSize);
        float travel = Math.max(0.0f, trackSize - thumbSize);
        if (scrollable <= 0.0f || travel <= 0.0f) {
            return 0.0f;
        }
        return Math.max(0.0f, Math.min(travel, scrollOffset / scrollable * travel));
    }

    @Override
    protected void renderBackGround(@Nonnull UIRenderContext context) {
        final float x = this.finalRect.x + this.finalRect.w * (1.0f - this.offsetAlpha);
        context.renderNineSliceSpriteWithAlpha(
                WEAssets.SLIDER_LINE,
                (int) x,
                (int) this.finalRect.y,
                (int) Math.ceil(this.finalRect.w * this.offsetAlpha),
                (int) (this.finalRect.h * this.offsetAlpha),
                this.renderAlpha
        );
    }

    @Override
    public void renderDebug(@Nonnull UIRenderContext context) {
        this.renderTrackOutline(context, this.topTrackRect);
        this.renderTrackOutline(context, this.rightTrackRect);
    }

    private void renderTrackOutline(@Nonnull UIRenderContext context, @Nonnull Rect trackRect) {
        context.renderOutline(
                (int) (this.finalRect.x + trackRect.x),
                (int) (this.finalRect.y + trackRect.y),
                (int) trackRect.w,
                (int) trackRect.h,
                UIColor.RED
        );
    }
}
