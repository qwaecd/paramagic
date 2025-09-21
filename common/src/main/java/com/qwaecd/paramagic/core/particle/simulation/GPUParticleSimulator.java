package com.qwaecd.paramagic.core.particle.simulation;

import com.qwaecd.paramagic.core.particle.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.data.ParticleVAO;
import com.qwaecd.paramagic.core.particle.memory.ParticleBufferSlice;
import com.qwaecd.paramagic.core.particle.simulation.emitter.Emitter;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;

import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class GPUParticleSimulator {
    private final Shader updateShader;

    public GPUParticleSimulator() {
        this.updateShader = ShaderManager.getInstance().getShaderThrowIfNotFound("particle_update");
    }

    public void update(
            float deltaTime,
            ParticleVAO vao,
            List<GPUParticleEffect> effects,
            int readVBO,
            int writeVBO
    ) {
        glEnable(GL_RASTERIZER_DISCARD);

        vao.bindAndConfigure(readVBO);
        glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, writeVBO);


        this.updateShader.bind();
        glBeginTransformFeedback(GL_POINTS);
        applyUniforms(deltaTime);
        for (GPUParticleEffect effect : effects) {
            updateEffect(deltaTime, effect);
        }


        glEndTransformFeedback();
        glDisable(GL_RASTERIZER_DISCARD);

        glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, 0);
        vao.unbind();
        this.updateShader.unbind();
    }

    private void updateEffect(float deltaTime, GPUParticleEffect effect) {
        Emitter emitter = effect.getEmitter();
        float particlesToEmit = emitter.getParticlesPerSecond() * deltaTime;
        effect.emissionDebt += particlesToEmit;
        int emitCount = (int) effect.emissionDebt;
        effect.emissionDebt -= emitCount;

        int baseIndex = effect.getActivationCounter();
        int particleCountInSlice = effect.getSlice().getParticleCount();
        effect.setActivationCounter( (baseIndex + emitCount) % particleCountInSlice );

        updateShader.setUniformValue1i("u_emitCount", emitCount);
        updateShader.setUniformValue1i("u_activationBaseIndex", baseIndex);
        updateShader.setUniformValue1i("u_particleCountInSlice", particleCountInSlice);

        emitter.update(deltaTime, this.updateShader);
        effect.applyCustomUniforms(this.updateShader);
        ParticleBufferSlice slice = effect.getSlice();
        glDrawArrays(GL_POINTS, slice.getOffset(), slice.getParticleCount());
    }

    private void applyUniforms(float deltaTime) {
        this.updateShader.setUniformValue1f("u_deltaTime", deltaTime);
    }
}
