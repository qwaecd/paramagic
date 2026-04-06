package com.qwaecd.paramagic.core.render.post;

import com.qwaecd.paramagic.client.renderbase.factory.FullScreenQuadFactory;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

import static org.lwjgl.opengl.GL33.*;

public class FinalComposePass implements AutoCloseable {
    private Shader composeShader;
    private Mesh fullscreenQuad;

    public void initialize() {
        this.composeShader = ShaderManager.getInstance().getShaderThrowIfNotFound("final_compose");
        this.fullscreenQuad = FullScreenQuadFactory.createFullscreenQuad();
    }

    public void combine(int hdrSceneTextureId, int gameSceneTextureId, float exposure, boolean gammaCorrection) {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

        composeShader.bind();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, hdrSceneTextureId);
        composeShader.setUniformValue1i("u_hdrSceneTexture", 0);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, gameSceneTextureId);
        composeShader.setUniformValue1i("u_gameSceneTexture", 1);

        composeShader.setUniformValue1f("u_exposure", exposure);
        composeShader.setUniformValue1i("u_enableGammaCorrection", gammaCorrection ? 1 : 0);
        fullscreenQuad.draw();
        composeShader.unbind();
    }

    @Override
    public void close() throws Exception {
    }
}
