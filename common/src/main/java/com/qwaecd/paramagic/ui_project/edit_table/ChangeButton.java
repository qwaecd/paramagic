package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.util.UIColor;

import javax.annotation.Nonnull;

public class ChangeButton extends UINode {
    private boolean pressed;

    public static final float BUTTON_SIZE = 32.0f;

    public ChangeButton() {
        this.localRect.setWH(BUTTON_SIZE, BUTTON_SIZE);
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
            context.drawQuad(this.worldRect, UIColor.fromRGBA(200, 200, 200, 100));
        } else {
            context.drawQuad(this.worldRect, UIColor.fromRGBA(127, 127, 127, 200));
        }
    }
}
