package com.qwaecd.paramagic.core.particle.emitter.property.type;

/**
 * Particle primitive type encoded into EmissionRequest.param6.z via intBitsToFloat.
 */
public enum ParticlePrimitiveTypeStates {
    POINT(0),
    TRIANGLE(1),
    QUAD(2);

    public final int value;

    ParticlePrimitiveTypeStates(int value) {
        this.value = value;
    }
}

