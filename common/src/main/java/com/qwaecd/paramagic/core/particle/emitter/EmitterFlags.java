package com.qwaecd.paramagic.core.particle.emitter;

@SuppressWarnings("PointlessBitwiseExpression")
public enum EmitterFlags {
    // #define EMITTER_FLAGS_NONE (0u)
    // #define EMITTER_FLAGS_EMIT_FROM_VOLUME (1u << 1)
    NONE(0 << 0),
    /**
     * 有该标志位则从体积内发射，否则从表面发射。
     */
    EMIT_FROM_VOLUME(1 << 1);

    private final int bit;
    EmitterFlags(int bit) {
        this.bit = bit;
    }
    public int get() {
        return this.bit;
    }
}
