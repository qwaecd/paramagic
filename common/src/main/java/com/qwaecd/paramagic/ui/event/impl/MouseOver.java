package com.qwaecd.paramagic.ui.event.impl;

import com.qwaecd.paramagic.ui.event.UIEvent;

public class MouseOver extends UIEvent {
    public final double mouseX;
    public final double mouseY;

    public MouseOver(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
