package com.qwaecd.paramagic.core.particle.effect;

@SuppressWarnings("PointlessBitwiseExpression")
public enum EffectFlags {
    // #define EFFECT_FLAG_IS_ALIVE    (1u << 0)
    // #define EFFECT_FLAG_KILL_ALL    (1u << 1)
    IS_ALIVE(1 << 0),
    KILL_ALL(1 << 1);

    private final int bit;
    EffectFlags(int bit) {
        this.bit = bit;
    }
    public int get() {
        return this.bit;
    }

    public boolean in(int flags) {
        return (flags & this.bit) != 0;
    }
}
