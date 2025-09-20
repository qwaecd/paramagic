package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.core.particle.memory.ParticleBufferSlice;
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
            List<ParticleBufferSlice> activeSlices,
            int readVBO,
            int writeVBO
    ) {
        applyUniforms(deltaTime);
        glEnable(GL_RASTERIZER_DISCARD);

        vao.bindAndConfigure(readVBO);
        glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, writeVBO);


        glBeginTransformFeedback(GL_POINTS);
        for (ParticleBufferSlice slice : activeSlices) {
            glDrawArrays(GL_POINTS, slice.getOffset(), slice.getParticleCount());
        }
        glEndTransformFeedback();

        glDisable(GL_RASTERIZER_DISCARD);

        glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, 0);
        vao.unbind();
        this.updateShader.unbind();
    }

    private void applyUniforms(float deltaTime) {
        this.updateShader.bind();
        this.updateShader.setUniformValue1f("u_deltaTime", deltaTime);
    }
}
