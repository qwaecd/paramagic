package com.qwaecd.paramagic.core.particle.emitter;

public enum EmitterType {
    POINT(1, "pointEmitterInitializer");

    public final int ID;
    public final String NAME = this.name();
    public final String SubroutineName;
    EmitterType(int id, String subroutineName) {
        this.ID = id;
        this.SubroutineName = subroutineName;
    }
}
