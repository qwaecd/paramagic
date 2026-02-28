package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import com.qwaecd.paramagic.ui.event.impl.MouseLeave;
import com.qwaecd.paramagic.ui.event.impl.MouseOver;
import com.qwaecd.paramagic.ui.io.mouse.MouseStateMachine;
import com.qwaecd.paramagic.ui.widget.ContextMenu;

public class EditContextMenu extends ContextMenu {
    public EditContextMenu() {
    }

    @Override
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
        context.stopPropagation();
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY, MouseStateMachine mouseState) {
    }
}
