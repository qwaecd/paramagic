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
    protected void arrangeSelf(float parentX, float parentY, float parentW, float parentH) {
        super.arrangeSelf(parentX, parentY, parentW, parentH);
        float maxX = parentX + parentW - this.finalRect.w;
        float maxY = parentY + parentH - this.finalRect.h;
        this.finalRect.x = Math.max(parentX, Math.min(this.finalRect.x, maxX));
        this.finalRect.y = Math.max(parentY, Math.min(this.finalRect.y, maxY));
        this.presentationRect.set(this.finalRect);
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        context.consumeAndStopPropagation();
    }

}
