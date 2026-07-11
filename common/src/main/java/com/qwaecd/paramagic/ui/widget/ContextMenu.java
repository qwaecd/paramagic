package com.qwaecd.paramagic.ui.widget;

import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;

public class ContextMenu extends UINode {

    public ContextMenu() {
    }

    public void cancel() {
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
//        final float screenW = UIManager.getWindowWidth() / UIManager.getGuiScale();
//        final float screenH = UIManager.getWindowHeight() / UIManager.getGuiScale();
//        if (this.localRect.x + this.localRect.w > screenW) {
//            this.localRect.x = screenW - this.localRect.w;
//        }
//        if (this.localRect.y + this.localRect.h > screenH) {
//            this.localRect.y = screenH - this.localRect.h;
//        }
//        super.layout(parentX, parentY, parentW, parentH);
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        context.consumeAndStopPropagation();
    }

}
