package com.qwaecd.paramagic.ui.event.impl;


public class DoubleClick extends MouseClick {
    public DoubleClick(double mouseX, double mouseY, int button) {
        super(mouseX, mouseY, button);
    }

    public DoubleClick(MouseClick clickEvent) {
        this(clickEvent.mouseX, clickEvent.mouseY, clickEvent.button);
    }
}
