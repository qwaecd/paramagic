package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.core.particle.simulation.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.memory.ParticleBufferSlice;
import com.qwaecd.paramagic.core.particle.render.renderer.ParticleRendererType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;


public class GPUParticleEffect {
    @Getter
    private final ParticleBufferSlice slice;
    @Getter
    private final ParticleRendererType rendererType;
    @Getter
    private final Emitter emitter;
    @Getter
    private final float maxLifetime;

    // --------------非有心力部分--------------
    /**
     * 重力
     */
    @Setter
    private Vector3f gravity;
    /**
     * 阻力
     */
    @Setter
    private float drag;
    // --------------有心力部分--------------
    // center force = A * pow(r, B) + C
    /**
     * 吸引点位置
     */
    @Setter
    private Vector3f attractorPosition;
    @Setter
    private float constantA = 0.0f;
    @Setter
    private float exponentB = -2.0f;
    @Setter
    private float constantC = 0.0f;


    public GPUParticleEffect(
            ParticleBufferSlice slice,
            Emitter emitter,
            float maxLifetime,
            ParticleRendererType rendererType
    ) {
        this.rendererType = rendererType;
        this.slice = slice;
        this.emitter = emitter;
        this.maxLifetime = maxLifetime;
        this.gravity = new Vector3f(0.0f, -0.05f, 0.0f);
        this.attractorPosition = new Vector3f(0.0f, 0.0f, 0.0f);
        this.drag = 0.05f;
    }

    /**
     * 所有力的强度大小单位都是 格/秒
     */
    public void applyCustomUniforms(Shader shader) {
        // --------------非有心力部分--------------
        shader.setUniformValue3f("u_gravity", this.gravity.x, this.gravity.y, this.gravity.z);
        shader.setUniformValue1f("u_drag", this.drag);
        // --------------有心力部分--------------
        // center force = A * pow(r, B) + C
        shader.setUniformValue3f("u_attractorPosition", this.attractorPosition.x, this.attractorPosition.y, this.attractorPosition.z);
        shader.setUniformValue1f("u_constantA", this.constantA);
        shader.setUniformValue1f("u_exponentB", this.exponentB);
        shader.setUniformValue1f("u_constantC", this.constantC);
    }

    public boolean isFinished() {
        return this.emitter.isFinished() && this.emitter.getTimeSinceFinished() >= this.maxLifetime;
    }
}
