package com.qwaecd.paramagic.core.particle.emitter;

public enum EmitterType {
    POINT(1, "pointEmitterInitializer"),
    LINE(2, "lineEmitterInitializer"),
    SPHERE(3, "sphereEmitterInitializer"),
    CUBE(4, "cubeEmitterInitializer"),
    CIRCLE(5, "circleEmitterInitializer");

    public final int id;
    public final String NAME = this.name();
    public final String SubroutineName;
    EmitterType(int id, String subroutineName) {
        this.id = id;
        this.SubroutineName = subroutineName;
    }

    public static EmitterType fromId(int id) {
        for (EmitterType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EmitterType id: " + id);
    }
}
