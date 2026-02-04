package com.qwaecd.paramagic.ui.event.impl;

import com.qwaecd.paramagic.ui.event.UIEvent;

public class DoubleClick extends UIEvent {
    public final double mouseX;
    public final double mouseY;
    public final int button;

    public DoubleClick(double mouseX, double mouseY, int button) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
    }

    public DoubleClick(MouseClick clickEvent) {
        this(clickEvent.mouseX, clickEvent.mouseY, clickEvent.button);
    }
}
