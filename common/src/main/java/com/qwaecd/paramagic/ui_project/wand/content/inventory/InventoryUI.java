package com.qwaecd.paramagic.ui_project.wand.content.inventory;

import com.qwaecd.paramagic.tools.anim.EasingFunction;
import com.qwaecd.paramagic.tools.anim.Interpolation;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.LayoutConstraints;
import com.qwaecd.paramagic.ui.core.MeasureResult;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public final class InventoryUI extends UINode {
    private float renderAlpha = 0.5f;

    public InventoryUI() {
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
        return MeasureResult.of(5.0f * 18.0f, 11.0f * 18.0f);
    }

    @Override
    protected void render(@NotNull UIRenderContext context) {
        float x = this.finalRect.x + this.finalRect.w / 2.0f * (1.0f - this.renderAlpha);
        float y = this.finalRect.y + this.finalRect.h / 2.0f * (1.0f - this.renderAlpha);
        context.renderNineSliceSprite(
                WEAssets.INVENTORY_RECT,
                (int) x,
                (int) y,
                (int) (this.finalRect.w * this.renderAlpha),
                (int) (this.finalRect.h * this.renderAlpha)
        );
    }
}
