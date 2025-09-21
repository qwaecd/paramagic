package com.qwaecd.paramagic.core.particle.simulation.impl;

import com.qwaecd.paramagic.core.particle.simulation.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.simulation.emitter.EmitterBase;
import com.qwaecd.paramagic.core.render.shader.Shader;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

public class PointEmitter extends EmitterBase implements Emitter {
    public PointEmitter(
            Vector3f position,
            float particlesPerSecond,
            Vector3f baseVelocity,
            float velocitySpread,
            float minLifetime,
            float maxLifetime
    ) {
        super(position, particlesPerSecond);
        this.baseVelocity = baseVelocity;
        this.velocitySpread = velocitySpread;
        this.minLifetime = minLifetime;
        this.maxLifetime = maxLifetime;
    }
    @Override
    public void initialize(final ByteBuffer buffer, final int particleCount) {
        float initialLifetime = 1.0f;
        float initialAge = initialLifetime + 1.0f;
        for (int i = 0; i < particleCount; i++) {
            buffer.putFloat(this.position.x).putFloat(this.position.y).putFloat(this.position.z); // position
            buffer.putFloat(this.baseVelocity.x).putFloat(this.baseVelocity.y).putFloat(this.baseVelocity.z); // velocity

            buffer.putFloat(initialAge); // age
            buffer.putFloat(initialLifetime); // lifetime

            buffer.putFloat(1.0f).putFloat(0.5f).putFloat(1.0f).putFloat(1.0f); // color rgba
            buffer.putFloat(0.0f); // intensity

            buffer.putFloat(1.0f); // size

            buffer.putFloat(0.0f); // angle
            buffer.putFloat(0.0f); // angularVelocity

            buffer.putInt(0); // index
        }
    }

    @Override
    public void update(float deltaTime, Shader shader) {
        if (isFinished()) {
            this.timeSinceFinished += deltaTime;
            return;
        }
        super.applyUpdateUniforms(shader);
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public float getTimeSinceFinished() {
        return this.timeSinceFinished;
    }

    @Override
    public float getParticlesPerSecond() {
        return this.particlesPerSecond;
    }

    public void stop() {
        if (!this.finished) {
            this.finished = true;
            this.timeSinceFinished = 0.0f;
        }
    }
}
