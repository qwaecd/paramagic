package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseLeave;
import com.qwaecd.paramagic.ui.event.impl.MouseOver;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;

public class ContextMenu extends UINode {

    public ContextMenu() {
    }

    public void cancel() {
    }

    @Override
    protected void onMouseOver(UIEventContext<MouseOver> context) {
    }

    @Override
    protected void onMouseLeave(UIEventContext<MouseLeave> context) {
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        MouseClick event = context.event;
        if (!this.hitTest((float) event.mouseX, (float) event.mouseY)) {
            context.manager.cancelContextMenu();
            return;
        }
        context.consume();
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY, MouseStateMachine mouseState) {
    }
}
