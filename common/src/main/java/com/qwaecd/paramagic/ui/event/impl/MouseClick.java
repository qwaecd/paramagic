package com.qwaecd.paramagic.ui.event.impl;

import com.qwaecd.paramagic.ui.event.UIEvent;

public class MouseClick extends UIEvent {
    public final double mouseX;
    public final double mouseY;
    public final int button;

    public MouseClick(double mouseX, double mouseY, int button) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
    }
}
