package com.qwaecd.paramagic.ui.event.impl;

import com.qwaecd.paramagic.ui.event.UIEvent;

public class WheelEvent extends UIEvent {
    public final double mouseX;
    public final double mouseY;
    public final double scrollDelta;

    public WheelEvent(double mouseX, double mouseY, double scrollDelta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.scrollDelta = scrollDelta;
    }
}
