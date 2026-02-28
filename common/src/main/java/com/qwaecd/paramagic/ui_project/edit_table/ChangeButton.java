package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.util.UIColor;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChangeButton extends UINode {
    private static final EditTableSprite selectedBackGround = new EditTableSprite(
            112, 1,
            26, 23,
            -5, -4
    );
    private static final EditTableSprite notSelectedBackGround = new EditTableSprite(
            138, 1,
            26, 23,
            -5, -4
    );

    private boolean pressed;

    public static final float BUTTON_SIZE = 18.0f;

    private final EditTableSprite sprite;

    @Nullable
    private Component text;

    public ChangeButton(EditTableSprite sprite) {
        this.localRect.setWH(BUTTON_SIZE, BUTTON_SIZE);
        this.sprite = sprite;
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
        if (this.text != null) {
            float textX = this.worldRect.x + (this.worldRect.w - context.getTextWidth(this.text)) / 2.0f;
            float textY = this.worldRect.y + (this.worldRect.h - context.getLineHeight()) / 2.0f;
            context.drawText(this.text, textX, textY, UIColor.WHITE);
        }
    }

    @Override
    protected void renderBackGround(UIRenderContext context) {
        if (this.pressed) {
            context.renderSprite(
                    selectedBackGround,
                    this.worldRect.x + selectedBackGround.spriteOffsetX,
                    this.worldRect.y + selectedBackGround.spriteOffsetY
            );
        } else {
            context.renderSprite(
                    notSelectedBackGround,
                    this.worldRect.x + notSelectedBackGround.spriteOffsetX,
                    this.worldRect.y + notSelectedBackGround.spriteOffsetY
            );
        }
        context.renderSprite(
                this.sprite,
                this.worldRect.x + sprite.spriteOffsetX,
                this.worldRect.y + sprite.spriteOffsetY
        );
    }
}
