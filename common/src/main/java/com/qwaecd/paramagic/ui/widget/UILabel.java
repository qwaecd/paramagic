package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.Rect;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class UILabel extends UINode {
    @Nonnull
    protected Component label;

    protected UIColor color = UIColor.WHITE;
    protected boolean dropShadow = false;

    protected boolean centered = true;

    public UILabel(@Nonnull Component label) {
        super();
        this.label = label;
    }

    public UILabel(@Nonnull String label) {
        super();
        this.label = Component.literal(label);
    }

    public UILabel(@Nonnull Component label, Rect local) {
        super();
        this.label = label;
        this.localRect.set(local);
    }

    public UILabel(@Nonnull String label, Rect local) {
        super();
        this.label = Component.literal(label);
        this.localRect.set(local);
    }

    @Override
    public void render(@Nonnull UIRenderContext context) {
        if (!this.visible) {
            return;
        }

        if (this.centered) {
            float textWidth = context.getTextWidth(this.label);
            float x = this.worldRect.x + (this.worldRect.w - textWidth) / 2.0f;
            context.drawText(this.label, x, this.worldRect.y, this.color, this.dropShadow);
        } else {
            context.drawText(this.label, this.worldRect.x, this.worldRect.y, this.color, this.dropShadow);
        }
        if (this.showDebugOutLine) {
            this.renderDebugOutLine(context);
        }
    }

    protected void renderDebugOutLine(UIRenderContext context) {
        if (this.centered) {
            float textWidth = context.getTextWidth(this.label);
            context.renderOutline(
                    (int) (this.worldRect.x + (this.worldRect.w - textWidth) / 2.0f), (int) this.worldRect.y - 1,
                    (int) textWidth, context.getLineHeight(),
                    UIColor.RED
            );
        } else {
            context.renderOutline(
                    (int) this.worldRect.x, (int) this.worldRect.y - 1,
                    context.getTextWidth(this.label), context.getLineHeight(),
                    UIColor.RED
            );
        }
    }

    public void setColor(UIColor color) {
        this.color = color;
    }

    public void setDropShadow(boolean b) {
        this.dropShadow = b;
    }

    public boolean isDropShadow() {
        return this.dropShadow;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }
}
