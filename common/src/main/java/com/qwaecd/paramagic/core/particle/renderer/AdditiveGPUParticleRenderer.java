package com.qwaecd.paramagic.core.particle.renderer;

import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.state.GLStateCache;
import com.qwaecd.paramagic.core.render.state.RenderState;
import org.joml.Matrix4f;

public class AdditiveGPUParticleRenderer extends ParticleRenderer {
    private final Shader renderShader;

    public AdditiveGPUParticleRenderer() {
        this.renderShader = ShaderManager.getInstance().getShaderThrowIfNotFound("particle_render");
    }
    @Override
    public void render(RenderContext context, GLStateCache stateCache) {
        stateCache.apply(RenderState.ADDITIVE);
        this.renderShader.bind();
        applyUniforms(context);
    }

    private void applyUniforms(RenderContext context) {
        Matrix4f projectionMatrix = context.getProjectionMatrix();
        Matrix4f viewMatrix = context.getMatrixStackProvider().getViewMatrix();
        this.renderShader.setUniformMatrix4f("u_projectionMatrix", projectionMatrix);
        this.renderShader.setUniformMatrix4f("u_viewMatrix", viewMatrix);
    }

    @Override
    public ParticleRendererType getType() {
        return ParticleRendererType.ADDITIVE;
    }

    @Override
    public void close() throws Exception {

    }
}
