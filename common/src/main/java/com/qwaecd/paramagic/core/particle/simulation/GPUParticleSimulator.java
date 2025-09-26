package com.qwaecd.paramagic.core.particle.simulation;

import com.qwaecd.paramagic.core.particle.GPUParticleEffect;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;

import java.util.List;

public class GPUParticleSimulator {
    private final Shader updateShader;

    public GPUParticleSimulator() {
        this.updateShader = ShaderManager.getInstance().getShaderNullable("particle_update");
    }

    public void update(
            float deltaTime,
            List<GPUParticleEffect> effects,
            int readVBO,
            int writeVBO
    ) {
    }
    private void applyUniforms(float deltaTime) {
        this.updateShader.setUniformValue1f("u_deltaTime", deltaTime);
    }
}
