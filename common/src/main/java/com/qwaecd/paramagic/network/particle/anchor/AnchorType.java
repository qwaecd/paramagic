package com.qwaecd.paramagic.network.particle.anchor;

public enum AnchorType {
    STATIC_POS(0),
    ENTITY(1),
    BLOCK(2);
    public final int id;
    AnchorType(int id) {
        this.id = id;
    }

    public static AnchorType fromId(int id) {
        for (AnchorType spec : values()) {
            if (spec.id == id) {
                return spec;
            }
        }
        throw new IllegalArgumentException("Invalid AnchorSpec id: " + id);
    }
}
