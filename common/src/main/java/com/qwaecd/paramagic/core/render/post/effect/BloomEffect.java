package com.qwaecd.paramagic.core.render.post.effect;

import com.qwaecd.paramagic.client.renderbase.factory.FullScreenQuadFactory;
import com.qwaecd.paramagic.core.render.post.buffer.SingleTargetFramebuffer;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.TextureUtils;
import com.qwaecd.paramagic.core.render.vertex.Mesh;

import static org.lwjgl.opengl.GL33.*;

public class BloomEffect implements IPostProcessingEffect {

    private Shader blurShader;
    private Mesh fullscreenQuad;
    private SingleTargetFramebuffer[] blurFbos;
    private SingleTargetFramebuffer pingPongFbo;
    private boolean enabled = true;
    @Override
    public void initialize(int width, int height) {
        this.blurShader = ShaderManager.getShaderThrowIfNotFound("blur");
        this.fullscreenQuad = FullScreenQuadFactory.createFullscreenQuad();
        pingPongFbo = new SingleTargetFramebuffer(width / 2, height / 2);
        blurFbos = new SingleTargetFramebuffer[4];
        for (int i = 0; i < blurFbos.length; i++) {
            int scale = 1 << (i + 1); // 2, 4, 8, 16
            blurFbos[i] = new SingleTargetFramebuffer(width / scale, height / scale);
        }
    }

    @Override
    public int apply(int inputTextureId) {
        if (!this.enabled) {
            return TextureUtils.getBlackTexture();
        }
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

        performBlur(inputTextureId, blurFbos[0]);
        for (int i = 1; i < blurFbos.length; i++) {
            SingleTargetFramebuffer sourceFbo = blurFbos[i - 1];
            SingleTargetFramebuffer destFbo = blurFbos[i];
            performBlur(sourceFbo.getColorTextureId(), destFbo);
        }


        if (pingPongFbo.getWidth() != blurFbos[0].getWidth() || pingPongFbo.getHeight() != blurFbos[0].getHeight()) {
            pingPongFbo.resize(blurFbos[0].getWidth(), blurFbos[0].getHeight());
        }

        pingPongFbo.bind();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        blurShader.bind();
        blurShader.setUniformValue1i("u_texture", 0);
        blurShader.setUniformValue1i("u_horizontal", 0);
        blurShader.setUniformValue2f("u_texelSize", 0.0f, 0.0f);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, blurFbos[0].getColorTextureId());
        fullscreenQuad.draw();
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        for (int i = 1; i < blurFbos.length; i++) {
            pingPongFbo.bind();

            // 读取原始的、未被污染的 blurFbos[i]
            glBindTexture(GL_TEXTURE_2D, blurFbos[i].getColorTextureId());
            fullscreenQuad.draw();
        }

        glDisable(GL_BLEND);
        return pingPongFbo.getColorTextureId();
    }

    /**
     * 对输入纹理执行一次完整的二维高斯模糊，并将结果存入目标FBO。
     * 使用乒乓技术。
     * @param inputTextureId 要模糊的源纹理
     * @param destinationFbo 存储最终结果的FBO
     */
    private void performBlur(int inputTextureId, SingleTargetFramebuffer destinationFbo) {
        // 确保我们的乒乓FBO和目标FBO尺寸一致
        if (pingPongFbo.getWidth() != destinationFbo.getWidth() || pingPongFbo.getHeight() != destinationFbo.getHeight()) {
            pingPongFbo.resize(destinationFbo.getWidth(), destinationFbo.getHeight());
        }
        blurShader.bind();
        blurShader.setUniformValue1i("u_texture", 0);
        glActiveTexture(GL_TEXTURE0);
        // Pass 1: 水平模糊 (Input -> PingPongFBO)
        pingPongFbo.bind();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        blurShader.setUniformValue2f("u_texelSize", 1.0f / pingPongFbo.getWidth(), 1.0f / pingPongFbo.getHeight());
        blurShader.setUniformValue1i("u_horizontal", 1);
        glBindTexture(GL_TEXTURE_2D, inputTextureId);
        fullscreenQuad.draw();
        // Pass 2: 垂直模糊 (PingPongFBO -> DestinationFBO)
        destinationFbo.bind();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        blurShader.setUniformValue1i("u_horizontal", 0);
        glBindTexture(GL_TEXTURE_2D, pingPongFbo.getColorTextureId());
        fullscreenQuad.draw();
    }

    @Override
    public void onResize(int newWidth, int newHeight) {
        pingPongFbo.resize(newWidth / 2, newHeight / 2);
        for (int i = 0; i < blurFbos.length; i++) {
            int scale = 1 << (i + 1);
            blurFbos[i].resize(newWidth / scale, newHeight / scale);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void close() throws Exception {
        if (blurFbos == null) {
            return;
        }
        if (pingPongFbo != null) {
            pingPongFbo.close();
        }
        for (SingleTargetFramebuffer fbo : blurFbos) {
            if (fbo != null) {
                fbo.close();
            }
        }
    }
}
