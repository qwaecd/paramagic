package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.*;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public final class TreeCanvas extends UINode {
    private float offsetAlpha = 0.6f;
    private float renderAlpha = 0.1f;

    private final TreeContent treeContent;

    private final Rect clipRect = new Rect();

    public TreeCanvas() {
        super();
        this.clipMod = ClipMod.RECT;
        this.treeContent = new TreeContent();
        this.addChild(this.treeContent);
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
    protected void arrangeSelf(float parentX, float parentY, float parentW, float parentH) {
        super.arrangeSelf(parentX, parentY, parentW, parentH);
        this.clipRect.set(
                this.finalRect.x + 8.0f, this.finalRect.y + 8.0f,
                this.finalRect.w - 16.0f, this.finalRect.h - 16.0f
        );
    }

    @Override
    protected void arrangeChildren() {
        Rect rect = this.treeContent.getLayoutRect();
        rect.setWH(this.treeContent.getMeasuredWidth(), this.treeContent.getMeasuredHeight());
        rect.setXY(8.0f, 8.0f);
        this.treeContent.arrange(this.finalRect.x, this.finalRect.y, this.finalRect.w, this.finalRect.h);
    }

    @Override
    @Nonnull
    public Rect getClipRect() {
        return this.clipRect;
    }

    @Override
    protected void render(@NotNull UIRenderContext context) {
    }

    @Override
    protected void renderBackGround(@Nonnull UIRenderContext context) {
        float x = this.finalRect.x + this.finalRect.w / 2.0f * (1.0f - this.offsetAlpha);
        float y = this.finalRect.y + this.finalRect.h / 2.0f * (1.0f - this.offsetAlpha);
        context.renderNineSliceSpriteWithAlpha(
                WEAssets.RECT_1,
                (int) x,
                (int) y,
                (int) (this.finalRect.w * this.offsetAlpha),
                (int) (this.finalRect.h * this.offsetAlpha),
                this.renderAlpha
        );
    }
}
