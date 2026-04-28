package com.qwaecd.paramagic.core.particle.emitter.property.type;

public enum ParticleShapeFlags {
    FIXED(0b00),
    JITTERED(0b01),
    RESERVED_2(0b10),
    RESERVED_3(0b11);

    public static final int MASK = 0b11;
    public static final int REQUEST_OFFSET = 4;
    public static final int REQUEST_MASK = MASK << REQUEST_OFFSET;

    private final int bits;

    ParticleShapeFlags(int bits) {
        this.bits = bits;
    }

    public int get() {
        return this.bits;
    }

    public boolean in(int mask) {
        return (mask & MASK) == this.bits;
    }

    public boolean inRequest(int requestMask) {
        return ((requestMask & REQUEST_MASK) >> REQUEST_OFFSET) == this.bits;
    }

    public int applyTo(int mask) {
        return (mask & ~MASK) | this.bits;
    }

    public int applyToRequest(int requestMask) {
        return (requestMask & ~REQUEST_MASK) | (this.bits << REQUEST_OFFSET);
    }
}
