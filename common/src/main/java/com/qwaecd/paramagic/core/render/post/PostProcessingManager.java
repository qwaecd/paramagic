package com.qwaecd.paramagic.core.render.post;


import com.qwaecd.paramagic.core.render.post.buffer.SingleTargetFramebuffer;
import com.qwaecd.paramagic.core.render.post.effect.BloomEffect;
import com.qwaecd.paramagic.core.render.post.effect.IPostProcessingEffect;
import com.qwaecd.paramagic.core.render.texture.TextureUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class PostProcessingManager implements AutoCloseable {
    private final List<IPostProcessingEffect> effects = new ArrayList<>();

    private CompositePass compositePass;

    private SingleTargetFramebuffer[] pingPongFbos = new SingleTargetFramebuffer[2];
    private int width;
    private int height;

    public void initialize(int width, int height) {
        this.width = width;
        this.height = height;

        pingPongFbos[0] = new SingleTargetFramebuffer(width, height);
        pingPongFbos[1] = new SingleTargetFramebuffer(width, height);

        compositePass = new CompositePass();
        compositePass.initialize();

        BloomEffect bloomEffect = new BloomEffect();
        bloomEffect.initialize(width, height);
        effects.add(bloomEffect);
    }

    /**
     * @return Paramagic HDR 合成纹理 id（与 {@link #processSceneTextures} 的 hdr 分量相同）
     */
    public int process(int sceneTextureId, int bloomSourceTextureId) {
        return processSceneTextures(sceneTextureId, bloomSourceTextureId).hdrModCompositeTextureId();
    }

    /**
     * 执行 bloom + composite + 非 Bloom 后处理链，并同时返回模糊 bloom 纹理 id（供 {@link com.qwaecd.paramagic.core.render.geometricmask.GeometricMaskInputPolicy#BLOOM_ONLY} 等使用）。
     */
    public PostProcessSceneTextures processSceneTextures(int sceneTextureId, int bloomSourceTextureId) {
        int finalBloomTexture = -1;
        for (IPostProcessingEffect effect : effects) {
            if (effect instanceof BloomEffect && effect.isEnabled()) {
                finalBloomTexture = effect.apply(bloomSourceTextureId);
            }
        }

        if (finalBloomTexture == -1) {
            finalBloomTexture = TextureUtils.getBlackTexture();
        }

        pingPongFbos[0].bind();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        compositePass.combine(sceneTextureId, finalBloomTexture, 1.0f);
        pingPongFbos[0].unbind();

        int readFboIndex = 0;
        for (IPostProcessingEffect effect : effects) {
            if (effect instanceof BloomEffect || !effect.isEnabled()) {
                continue;
            }

            int writeFboIndex = 1 - readFboIndex;
            pingPongFbos[writeFboIndex].bind();
            glClear(GL_COLOR_BUFFER_BIT);

            int inputTexture = pingPongFbos[readFboIndex].getColorTextureId();
            effect.apply(inputTexture);
            pingPongFbos[writeFboIndex].unbind();

            readFboIndex = writeFboIndex;
        }
        int hdrOut = pingPongFbos[readFboIndex].getColorTextureId();
        return new PostProcessSceneTextures(hdrOut, finalBloomTexture);
    }

    public void onResize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
        for (SingleTargetFramebuffer fbo : pingPongFbos) {
            fbo.resize(newWidth, newHeight);
        }
        for (IPostProcessingEffect effect : effects) {
            effect.onResize(newWidth, newHeight);
        }
    }


    @Override
    public void close() throws Exception {
        for (SingleTargetFramebuffer fbo : pingPongFbos) {
            fbo.close();
        }
        if (compositePass != null) {
            compositePass.close();
        }
        for (IPostProcessingEffect effect : effects) {
            if (effect != null) {
                effect.close();
            }
        }
        effects.clear();
    }
}
