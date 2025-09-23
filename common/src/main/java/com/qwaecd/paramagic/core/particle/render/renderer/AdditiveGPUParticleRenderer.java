package com.qwaecd.paramagic.core.particle.render.renderer;

import com.qwaecd.paramagic.core.particle.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.ParticleManager;
import com.qwaecd.paramagic.core.particle.data.InstancedParticleVAO;
import com.qwaecd.paramagic.core.particle.data.ParticleMesh;
import com.qwaecd.paramagic.core.particle.data.ParticleMeshes;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.state.GLStateCache;
import com.qwaecd.paramagic.core.render.state.RenderState;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL33.*;

public class AdditiveGPUParticleRenderer extends ParticleRenderer {
    private final Shader renderShader;

    public AdditiveGPUParticleRenderer() {
        this.renderShader = ShaderManager.getInstance().getShaderThrowIfNotFound("particle_render");
    }
    @Override
    public void render(RenderContext context, GPUParticleEffect effect, GLStateCache stateCache, InstancedParticleVAO instancedParticleVAO) {
        stateCache.apply(RenderState.ADDITIVE);
        this.renderShader.bind();
        applyUniforms(context);

        int readVBO = ParticleManager.getInstance().getCurrentReadVBO();
        instancedParticleVAO.bindAndConfigure(readVBO);
        ParticleMesh mesh = ParticleMeshes.get(ParticleMesh.ParticleMeshType.QUAD);

        int indexCount = mesh.getIndexCount();
        int particleCount = effect.getSlice().getParticleCount();
        long indexOffset = 0L;
        glDisable(GL_CULL_FACE);
        glDrawElementsInstanced(
                GL_TRIANGLES,
                indexCount,
                GL_UNSIGNED_INT,
                indexOffset,
                particleCount
        );
        glEnable(GL_CULL_FACE);
        instancedParticleVAO.unbind();
    }

    private void applyUniforms(RenderContext context) {
        Matrix4f projectionMatrix = context.getProjectionMatrix();
        Matrix4f viewMatrix = context.getMatrixStackProvider().getViewMatrix();
        this.renderShader.setUniformMatrix4f("u_projectionMatrix", projectionMatrix);
        this.renderShader.setUniformMatrix4f("u_viewMatrix", viewMatrix);
        this.renderShader.setUniformValue3f("u_cameraPosition", (float) context.getCamera().position().x, (float) context.getCamera().position().y, (float) context.getCamera().position().z);
    }

    @Override
    public ParticleRendererType getType() {
        return ParticleRendererType.ADDITIVE;
    }
}
