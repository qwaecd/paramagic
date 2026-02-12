package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.UIColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class UILabel extends UINode {
    public static final String pangram = "The quick brown fox jumps over a lazy dog.";
    protected final FontSizeProvider provider = new FontSizeProvider() {
        private final Font fontInstance = Minecraft.getInstance().gui.getFont();
        @Override
        public int getTextWidth(String text) {
            return fontInstance.width(text);
        }

        @Override
        public int getTextWidth(Component text) {
            return fontInstance.width(text);
        }

        @Override
        public int getLineHeight() {
            return fontInstance.lineHeight;
        }
    };

    @Nonnull
    protected Component label;

    protected UIColor color = UIColor.WHITE;
    protected boolean dropShadow = false;

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
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        final int textWidth = this.provider.getTextWidth(this.label);
        final int lineHeight = this.provider.getLineHeight();
        this.localRect.setWH(textWidth, lineHeight);
        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    public void render(@Nonnull UIRenderContext context) {
        if (!this.visible) {
            return;
        }
        context.drawText(this.label, this.worldRect.x, this.worldRect.y, this.color, this.dropShadow);
    }

    @Override
    public void renderDebug(@Nonnull UIRenderContext context) {
        context.renderOutline(
                (int) this.worldRect.x - 1, (int) this.worldRect.y - 1,
                context.getTextWidth(this.label) + 1, context.getLineHeight() + 1,
                UIColor.RED
        );
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

    protected interface FontSizeProvider {
        int getTextWidth(String text);
        int getTextWidth(Component text);
        int getLineHeight();
    }
}
