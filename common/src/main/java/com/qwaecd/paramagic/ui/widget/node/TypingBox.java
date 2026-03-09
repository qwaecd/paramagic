package com.qwaecd.paramagic.ui.widget.node;

import com.qwaecd.paramagic.ui.api.event.AllUIEvents;
import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.MCEditBox;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class TypingBox extends UINode {
    @Nonnull
    private final MCEditBox box;

    public TypingBox() {
        this.box = new MCEditBox(
                (int) this.localRect.x, (int) this.localRect.y,
                (int) this.localRect.w, (int) this.localRect.h,
                Component.empty()
        );

        UIManager manager = UIManager.getInstance();
        if (manager != null) {
            manager.addMCWidget(() -> this.box);
        }
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        context.consume();
        context.allowMCProcessing(true);
    }

    @Override
    protected void onDoubleClick(UIEventContext<DoubleClick> context) {
        this.onMouseClick(UIEventContext.upcast(AllUIEvents.MOUSE_CLICK, context));
    }

    public void setText(String text) {
        this.box.setValue(text);
    }

    public String getText() {
        return this.box.getValue();
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        super.layout(parentX, parentY, parentW, parentH);
        this.box.resize(this.worldRect);
    }
}
