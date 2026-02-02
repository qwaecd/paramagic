package com.qwaecd.paramagic.ui.io.keyboard;

public enum ModifierKey {
    SHIFT(1),
    CTRL(2),
    ALT(4),

    SHIFT_CTRL(1 + 2),
    SHIFT_ALT(1 + 4),
    CTRL_ALT(2 + 3)
    ;
    public final int keyId;
    ModifierKey(int keyId) {
        this.keyId = keyId;
    }

    public static ModifierKey fromKeyId(int keyId) {
        for (ModifierKey key : values()) {
            if (key.keyId == keyId) {
                return key;
            }
        }
        return null;
    }
}
