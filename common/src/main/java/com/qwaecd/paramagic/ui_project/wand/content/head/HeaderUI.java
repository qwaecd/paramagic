package com.qwaecd.paramagic.ui_project.wand.content.head;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import com.qwaecd.paramagic.ui_project.wand.WandEditScreen;

import javax.annotation.Nonnull;

public final class HeaderUI extends UINode {
    private float offsetAlpha = 0.0f;

    public HeaderUI() {
        super();
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
    }

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        float w = WandEditScreen.WIDTH * 0.8f;
        float h = WEAssets.HEAD_LINE.getHeight();
        return MeasureResult.of(w, h);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        float x = this.finalRect.x + this.finalRect.w / 2.0f * (1.0f - this.offsetAlpha);
        context.renderHorizontalSliceSpriteKeepEnds(
                WEAssets.HEAD_LINE,
                (int) x,
                (int) this.finalRect.y,
                (int) (this.finalRect.w * this.offsetAlpha),
                (int) this.finalRect.h
        );
    }
}
