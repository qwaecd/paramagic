package com.qwaecd.paramagic.ui.event.impl;

import com.qwaecd.paramagic.ui.event.UIEvent;

public class MouseLeave extends UIEvent {
    public final double mouseX;
    public final double mouseY;

    public MouseLeave(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
