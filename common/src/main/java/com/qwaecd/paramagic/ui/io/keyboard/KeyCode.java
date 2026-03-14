package com.qwaecd.paramagic.ui.io.keyboard;

import javax.annotation.Nonnull;

public enum KeyCode {
    ESC(256);
    public final int code;
    KeyCode(int code) {
        this.code = code;
    }

    public static boolean is(@Nonnull KeyCode keyCode, int code) {
        return keyCode.code == code;
    }
}
