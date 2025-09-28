package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.compute.ComputeShader;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class PointEmitter extends EmitterBase implements Emitter {

    public PointEmitter(Vector3f position, float particlesPerSecond) {
        super(position, particlesPerSecond);
    }

    @Override
    public void update(float deltaTime, ComputeShader updateShader) {

    }

    @Override
    public @Nullable EmissionRequest getEmissionRequest() {
        return null;
    }

    @Override
    public EmitterType getType() {
        return EmitterType.POINT;
    }
}
