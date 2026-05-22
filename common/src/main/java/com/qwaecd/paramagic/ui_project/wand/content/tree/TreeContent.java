package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;

import javax.annotation.Nonnull;

public final class TreeContent extends UINode {
    private float renderAlpha = 0.5f;

    public TreeContent() {
        super();
    }

    @Override
    protected void onAttached(@Nonnull UIManager manager) {
        this.animateFloat(
                this.renderAlpha,
                1.0f,
                0.4f,
                EasingFunction.easeOutSine,
                Interpolation::liner,
                (v -> this.renderAlpha = v)
        );
    }

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        return MeasureResult.of(260.0f, 210.0f);
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        float x = this.finalRect.x + this.finalRect.w / 2.0f * (1.0f - this.renderAlpha);
        float y = this.finalRect.y + this.finalRect.h / 2.0f * (1.0f - this.renderAlpha);
        context.renderNineSliceSprite(
                WEAssets.RECT_1,
                (int) x,
                (int) y,
                (int) (this.finalRect.w * this.renderAlpha),
                (int) (this.finalRect.h * this.renderAlpha)
        );
    }
}
