package com.qwaecd.paramagic.core.particle.emitter.property.type;

/**
 * Particle facing mode encoded into EmissionRequest.param6.w via intBitsToFloat.
 */
public enum ParticleFacingModeStates {
    CAMERA_FACING(0),
    NORMAL_FACING(1);

    public final int value;

    ParticleFacingModeStates(int value) {
        this.value = value;
    }
}

