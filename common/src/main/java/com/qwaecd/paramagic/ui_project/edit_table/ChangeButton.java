package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.util.NineSliceSprite;
import com.qwaecd.paramagic.ui.util.UIColor;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChangeButton extends UINode {
    /**
     * <pre>
     * ┌───┬─────┬───┐
     * │C0 │ C1  │C2 │
     * ├───┼─────┼───┤
     * │C3 │ C4  │C5 │
     * ├───┼─────┼───┤
     * │C6 │ C7  │C8 │
     * └───┴─────┴───┘
     * </pre>
     */
    protected static final NineSliceSprite buttonSprite =
            NineSliceSprite.builder(ModRL.inModSpace("textures/gui/edit_table.png"), EditTableSprite.TEX_W, EditTableSprite.TEX_H)
                    .slice(0, 0,  224, 16, 16)
                    .slice(1, 16, 224, 32, 16)
                    .slice(2, 48, 224, 16, 16)
                    .slice(3, 0,  240, 16, 16)
                    .slice(4, 16, 240, 32, 16)
                    .slice(5, 48, 240, 16, 16)
                    .slice(6, 0,  256, 16, 16)
                    .slice(7, 16, 256, 32, 16)
                    .slice(8, 48, 256, 16, 16)
                    .build();

    private boolean pressed;

    public static final float BUTTON_SIZE = 16.0f;

    @Nullable
    private Component text;

    public ChangeButton() {
        this.localRect.setWH(BUTTON_SIZE * 3.0f, BUTTON_SIZE * 1.5f);
    }

    public void setText(@Nullable Component text) {
        this.text = text;
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        if (this.pressed) {
            return;
        }
        this.pressed = true;
        context.consumeAndStopPropagation();
    }

    @Override
    protected void onDoubleClick(UIEventContext<DoubleClick> context) {
        this.onMouseClick(UIEventContext.upcast(AllUIEvents.MOUSE_CLICK, context));
    }

    void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    @Override
    public void render(@Nonnull UIRenderContext context) {
        super.render(context);
        if (this.pressed) {
            context.renderOutline(this.worldRect, UIColor.fromRGBA(255, 255, 255, 200));
        }
        if (this.text != null) {
            float textX = this.worldRect.x + (this.worldRect.w - context.getTextWidth(this.text)) / 2.0f;
            float textY = this.worldRect.y + (this.worldRect.h - context.getLineHeight()) / 2.0f;
            context.drawText(this.text, textX, textY, UIColor.WHITE);
        }
    }

    @Override
    protected void renderBackGround(UIRenderContext context) {
        context.renderNineSliceSprite(
                buttonSprite,
                (int) this.worldRect.x, (int) this.worldRect.y,
                (int) this.worldRect.w, (int) this.worldRect.h
        );
    }
}
