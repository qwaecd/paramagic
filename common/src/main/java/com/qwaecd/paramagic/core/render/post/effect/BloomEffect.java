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
    private Shader bloomCompositeShader;
    private Mesh fullscreenQuad;
    private SingleTargetFramebuffer[] blurMipChain;
    private SingleTargetFramebuffer internalPingPongFbo;
    private boolean enabled = true;
    private final int blurPasses = 4;
    @Override
    public void initialize(int width, int height) {
        this.blurShader = ShaderManager.getInstance().getShaderThrowIfNotFound("blur");
        this.bloomCompositeShader = ShaderManager.getInstance().getShaderThrowIfNotFound("bloom_composite");
        this.fullscreenQuad = FullScreenQuadFactory.createFullscreenQuad();
        internalPingPongFbo = new SingleTargetFramebuffer(16, 16);
        blurMipChain = new SingleTargetFramebuffer[blurPasses];
        for (int i = 0; i < blurPasses; i++) {
            int scale = 1 << (i + 1); // 2, 4, 8, 16
            blurMipChain[i] = new SingleTargetFramebuffer(width / scale, height / scale);
        }
    }

    @Override
    public int apply(int inputTextureId) {
        if (!this.enabled) {
            return TextureUtils.getBlackTexture();
        }
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        // Pass 1: 近景辉光 (小半径，产生清晰核心)
        // 结果存储在 blurMipChain[0]
        performBlur(inputTextureId, blurMipChain[0], 2, 1.0f);
        // Pass 2: 远景辉光 (大半径，产生柔和光晕)
        // 结果存储在 blurMipChain[1]
        // 注意：输入源仍然是原始高光图 inputTextureId
        performBlur(inputTextureId, blurMipChain[1], 4, 1.5f);
        // --- 2. 混合与降采样 ---
        // 将远景辉光(blurMipChain[1])叠加到近景辉光(blurMipChain[0])上
        blurMipChain[0].bind();
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE); // 加法混合

        bloomCompositeShader.bind();
        bloomCompositeShader.setUniformValue1i("u_texture", 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, blurMipChain[1].getColorTextureId());
        fullscreenQuad.draw();

        glDisable(GL_BLEND);
        glActiveTexture(GL_TEXTURE0);
        for (int i = 1; i < blurPasses; i++) {
            SingleTargetFramebuffer sourceFbo = blurMipChain[i - 1];
            SingleTargetFramebuffer destFbo = blurMipChain[i];
            destFbo.bind();
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            glBindTexture(GL_TEXTURE_2D, sourceFbo.getColorTextureId());
            fullscreenQuad.draw();
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        // 2. --- 升采样与混合 (Upsampling) ---
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE); // 使用加法混合
        glActiveTexture(GL_TEXTURE0);
        // 从第二小的模糊层级开始，将其混合到下一个更大的层级上
        for (int i = blurPasses - 1; i > 0; i--) {
            SingleTargetFramebuffer sourceFbo = blurMipChain[i]; // 小的
            SingleTargetFramebuffer destFbo = blurMipChain[i - 1]; // 大的
            destFbo.bind();
            // 绑定小的纹理并绘制，它会被拉伸（升采样）并与destFbo中已有的内容混合
            glBindTexture(GL_TEXTURE_2D, sourceFbo.getColorTextureId());
            fullscreenQuad.draw();
        }
        glDisable(GL_BLEND);
        // 最终结果现在存储在最大的一级模糊FBO中
        return blurMipChain[0].getColorTextureId();
    }

    /**
     * 对输入纹理执行指定次数的二维高斯模糊，并将最终结果存入目标FBO。
     * 使用乒乓技术在 destinationFbo 和 internalPingPongFbo 之间来回绘制。
     *
     * @param inputTextureId 要模糊的源纹理
     * @param destinationFbo 存储最终结果的FBO
     * @param iterations     模糊迭代次数。每次迭代包含一次水平和一次垂直模糊。
     */
    private void performBlur(int inputTextureId, SingleTargetFramebuffer destinationFbo, int iterations, float blurRadius) {
        if (iterations <= 0) {
            // 如果不模糊，直接将输入拷贝到输出并返回
            destinationFbo.bind();
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            bloomCompositeShader.bind();
            bloomCompositeShader.setUniformValue1i("u_texture", 0);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, inputTextureId);
            fullscreenQuad.draw();
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            return;
        }
        if (internalPingPongFbo.getWidth() != destinationFbo.getWidth() || internalPingPongFbo.getHeight() != destinationFbo.getHeight()) {
            internalPingPongFbo.resize(destinationFbo.getWidth(), destinationFbo.getHeight());
        }
        blurShader.bind();
        blurShader.setUniformValue1i("u_texture", 0);
        blurShader.setUniformValue1f("u_blurRadius", blurRadius);
        glActiveTexture(GL_TEXTURE0);

        boolean horizontal = true;
        boolean firstPass = true;
        int passCount = iterations * 2;

        for (int i = 0; i < passCount; i++) {
            SingleTargetFramebuffer writeFbo = (i % 2 == 0) ? internalPingPongFbo : destinationFbo;

            writeFbo.bind();
            glClear(GL_COLOR_BUFFER_BIT);
            if (firstPass) {
                glBindTexture(GL_TEXTURE_2D, inputTextureId);
                firstPass = false;
            } else {
                SingleTargetFramebuffer readFbo = (i % 2 == 0) ? destinationFbo : internalPingPongFbo;
                glBindTexture(GL_TEXTURE_2D, readFbo.getColorTextureId());
            }
            blurShader.setUniformValue1i("u_horizontal", horizontal ? 1 : 0);
            if (horizontal) {
                blurShader.setUniformValue2f("u_texelSize", 1.0f / writeFbo.getWidth(), 0.0f);
            } else {
                blurShader.setUniformValue2f("u_texelSize", 0.0f, 1.0f / writeFbo.getHeight());
            }

            fullscreenQuad.draw();
            horizontal = !horizontal;
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void onResize(int newWidth, int newHeight) {
        for (int i = 0; i < blurPasses; i++) {
            int scale = 1 << (i + 1);
            blurMipChain[i].resize(newWidth / scale, newHeight / scale);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void close() throws Exception {
        if (internalPingPongFbo != null) {
            internalPingPongFbo.close();
        }
        for (SingleTargetFramebuffer fbo : blurMipChain) {
            if (fbo != null) {
                fbo.close();
            }
        }
    }
}
