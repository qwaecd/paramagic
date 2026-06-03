package com.qwaecd.paramagic.ui_project.wand.content.tree;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.UIColor;
import com.qwaecd.paramagic.ui_project.wand.WEAssets;

import javax.annotation.Nonnull;

public final class TreeScrollbarThumb extends UINode {
    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    @Nonnull
    private final Orientation orientation;
    private float renderAlpha = 1.0f;

    public TreeScrollbarThumb(@Nonnull Orientation orientation) {
        super();
        this.orientation = orientation;
    }

    public void setRenderAlpha(float renderAlpha) {
        this.renderAlpha = renderAlpha;
    }

    @Override
    protected void render(@Nonnull UIRenderContext context) {
        if (this.renderAlpha <= 0.0f) {
            return;
        }
        if (this.orientation == Orientation.HORIZONTAL) {
            context.renderSpriteWithAlpha(
                    WEAssets.SLIDER_HOR,
                    this.finalRect.x + (this.finalRect.w - WEAssets.SLIDER_HOR.width) / 2.0f,
                    this.finalRect.y + (this.finalRect.h - WEAssets.SLIDER_HOR.height) / 2.0f,
                    this.renderAlpha
            );
            return;
        }
        context.renderSpriteWithAlpha(
                WEAssets.SLIDER_VER,
                this.finalRect.x + (this.finalRect.w - WEAssets.SLIDER_VER.width) / 2.0f,
                this.finalRect.y + (this.finalRect.h - WEAssets.SLIDER_VER.height) / 2.0f,
                this.renderAlpha
        );
    }

    @Override
    public void renderDebug(@Nonnull UIRenderContext context) {
        context.renderOutline(this.finalRect, UIColor.BLUE);
    }
}
