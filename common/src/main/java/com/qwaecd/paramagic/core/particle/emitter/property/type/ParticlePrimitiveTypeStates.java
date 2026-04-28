package com.qwaecd.paramagic.core.particle.emitter.property.type;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;

/**
 * Particle primitive type encoded into EmissionRequest.param6.z via intBitsToFloat.
 */
public enum ParticlePrimitiveTypeStates implements IDataSerializable {
    POINT(0),
    TRIANGLE(1),
    QUAD(2);

    public final int value;

    ParticlePrimitiveTypeStates(int value) {
        this.value = value;
    }

    public static ParticlePrimitiveTypeStates fromValue(int value) {
        for (ParticlePrimitiveTypeStates state : values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown ParticlePrimitiveTypeStates value: " + value);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("particlePrimitiveType", this.value);
    }

    public static ParticlePrimitiveTypeStates fromCodec(DataCodec codec) {
        return fromValue(codec.readInt("particlePrimitiveType"));
    }
}
