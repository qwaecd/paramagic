package com.qwaecd.paramagic.ui_project.edit_table.cache.label;

import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.event.impl.WheelEvent;
import com.qwaecd.paramagic.ui.widget.node.TypingBox;

import javax.annotation.Nullable;

public class NumericTypingBox extends TypingBox {
    @Nullable
    private WheelHandler wheelHandler;

    public void setWheelHandler(@Nullable WheelHandler wheelHandler) {
        this.wheelHandler = wheelHandler;
    }

    @Override
    protected void onMouseScroll(UIEventContext<WheelEvent> context) {
        if (this.wheelHandler != null && this.wheelHandler.handle(this, context.event.scrollDelta)) {
            context.consumeAndStopPropagation();
        }
    }

    @FunctionalInterface
    public interface WheelHandler {
        boolean handle(NumericTypingBox box, double scrollDelta);
    }
}
