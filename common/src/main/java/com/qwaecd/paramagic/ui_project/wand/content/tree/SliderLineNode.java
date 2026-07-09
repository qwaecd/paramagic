package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import org.jetbrains.annotations.NotNull;

public class SliderLineNode extends UINode {
    private float renderAlpha = 0.0f;

    public SliderLineNode() {
    }

    @Override
    protected void onAttached(@NotNull UIManager manager) {
        this.animateFloat(
                this.renderAlpha,
                1.0f,
                0.8f,
                EasingFunction.easeOutSine,
                Interpolation::linear,
                (v -> {
                    if (v < 0.2f)
                        this.renderAlpha = 0.0f;
                    else
                        this.renderAlpha = v;
                })
        );
    }

    @Override
    protected MeasureResult measureSelf(LayoutConstraints constraints) {
        final float w = constraints.getMaxWidth() - 16.0f;
        final float h = constraints.getMaxHeight() - 20.0f;
        return MeasureResult.of(w, h);
    }

    @Override
    protected void arrangeSelf(float parentX, float parentY, float parentW, float parentH) {
        this.layoutRect.set(
                8.0f, 10.0f,
                this.getMeasuredWidth(), this.getMeasuredHeight()
        );
        super.arrangeSelf(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void render(@NotNull UIRenderContext context) {
        if (this.renderAlpha <= 0.0f) {
            return;
        }
        context.renderNineSliceSpriteWithAlpha(
                WEAssets.SLIDER_LINE,
                (int) this.finalRect.x, (int) this.finalRect.y,
                (int) this.finalRect.w, (int) this.finalRect.h,
                this.renderAlpha
        );
    }
}
