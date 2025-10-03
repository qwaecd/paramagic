package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.compute.ComputeShader;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BARRIER_BIT;

public class PointEmitter extends EmitterBase implements Emitter {
    private float particlesToEmitAccumulated = 0.0f;
    private final EmissionRequest request;

    public PointEmitter(Vector3f position, float particlesPerSecond) {
        super(position, particlesPerSecond);
        this.request = new EmissionRequest(
                0,
                EmitterType.POINT.ID,
                0,
                new Vector4f(position.x, position.y, position.z, 0), // param1: 发射源位置 (xyz)
                new Vector4f(0.0f, 0.5f, 0.0f, 0), // param2: 基础速度或方向 (xyz)
                new Vector4f(1.0f, 0.6f, 0.3f, 1.0f), // param3: 颜色 (rgba)
                new Vector4f(30.0f, 300.0f, 1.0f, 1.8f), // param4: 粒子生命周期(min, max), 尺寸(min, max)
                new Vector4f()  // param5: (for POINT) 发射角度
        );
    }

    @Override
    public void update(float deltaTime, ComputeShader updateShader) {
        this.particlesToEmitAccumulated += this.particlesPerSecond * deltaTime;
        // TODO: 暂时直接启动 updateShader 用于调试
        updateShader.bind();
        updateShader.setUniformValue1i("u_maxParticles", 1_000_000);
        updateShader.setUniformValue1f("u_deltaTime", deltaTime);
        updateShader.setUniformValue1f("CF_A", 4.0f);
        updateShader.setUniformValue1f("CF_B", -2.0f);
        updateShader.setUniformValue3f("u_centerForcePos", 5, 130, 0);
        updateShader.dispatch(1_000_000, 1, 1);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);

        updateShader.unbind();
    }

    @Override
    public @Nullable EmissionRequest getEmissionRequest() {
        if (this.particlesToEmitAccumulated >= 1.0f) {
            int particlesToEmit = (int) this.particlesToEmitAccumulated;
            this.particlesToEmitAccumulated -= particlesToEmit;
            this.request.setCount(particlesToEmit);
            this.setRequestPos(this.position);
            return this.request;
        }
        return null;
    }

    @Override
    public EmitterType getType() {
        return EmitterType.POINT;
    }

    private void setRequestPos(Vector3f v) {
        this.request.getParam1().set(v.x, v.y, v.z);
    }
}
