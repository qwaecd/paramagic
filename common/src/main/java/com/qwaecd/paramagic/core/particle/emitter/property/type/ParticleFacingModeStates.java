package com.qwaecd.paramagic.core.particle.emitter.property.type;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;

/**
 * Particle facing mode encoded into EmissionRequest.param6.w via intBitsToFloat.
 */
public enum ParticleFacingModeStates implements IDataSerializable {
    CAMERA_FACING(0),
    NORMAL_FACING(1);

    public final int value;

    ParticleFacingModeStates(int value) {
        this.value = value;
    }

    public static ParticleFacingModeStates fromValue(int value) {
        for (ParticleFacingModeStates state : values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown ParticleFacingModeStates value: " + value);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("particleFacingMode", this.value);
    }

    public static ParticleFacingModeStates fromCodec(DataCodec codec) {
        return fromValue(codec.readInt("particleFacingMode"));
    }
}
