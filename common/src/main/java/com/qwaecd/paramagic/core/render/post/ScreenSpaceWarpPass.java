package com.qwaecd.paramagic.core.render.post;

import com.qwaecd.paramagic.client.renderbase.factory.FullScreenQuadFactory;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

import static org.lwjgl.opengl.GL33.*;

public class ScreenSpaceWarpPass implements AutoCloseable {
    private Shader warpShader;
    private Mesh fullscreenQuad;

    public void initialize() {
        this.warpShader = ShaderManager.getInstance().getShaderThrowIfNotFound("screen_warp");
        this.fullscreenQuad = FullScreenQuadFactory.createFullscreenQuad();
    }

    public void warp(int sceneTextureId, int distortionFieldTextureId) {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

        warpShader.bind();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, sceneTextureId);
        warpShader.setUniformValue1i("u_sceneTexture", 0);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, distortionFieldTextureId);
        warpShader.setUniformValue1i("u_distortionFieldTexture", 1);

        fullscreenQuad.draw();
        warpShader.unbind();
    }

    @Override
    public void close() throws Exception {
    }
}
