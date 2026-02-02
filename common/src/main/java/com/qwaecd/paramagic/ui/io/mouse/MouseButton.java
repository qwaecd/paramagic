package com.qwaecd.paramagic.ui.io.mouse;

public enum MouseButton {
    NULL(-114514),
    LEFT(0),
    RIGHT(1),
    MIDDLE(2),
    BUTTON4(3),
    BUTTON5(4);

    public final int code;

    MouseButton(int code) {
        this.code = code;
    }
}
