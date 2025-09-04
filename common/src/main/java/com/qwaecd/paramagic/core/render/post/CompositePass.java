package com.qwaecd.paramagic.core.render.post;

import com.qwaecd.paramagic.client.renderbase.factory.FullScreenQuadFactory;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

import static org.lwjgl.opengl.GL33.*;

public class CompositePass implements AutoCloseable {

    private Shader compositeShader;
    private Mesh fullscreenQuad;
    public void initialize() {
        this.compositeShader = ShaderManager.getInstance().getCompositeShader();
        this.fullscreenQuad = FullScreenQuadFactory.createFullscreenQuad();
    }

    public void combine(int sceneTextureId, int bloomTextureId, float bloomStrength) {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

        compositeShader.bind();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, sceneTextureId);
        compositeShader.setUniformValue1i("u_sceneTexture", 0);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, bloomTextureId);
        compositeShader.setUniformValue1i("u_bloomTexture", 1);
        compositeShader.setUniformValue1f("u_bloomStrength", bloomStrength);
        fullscreenQuad.draw();
    }

    @Override
    public void close() throws Exception {

    }
}
