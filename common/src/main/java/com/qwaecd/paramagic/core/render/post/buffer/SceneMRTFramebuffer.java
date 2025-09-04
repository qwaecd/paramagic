package com.qwaecd.paramagic.core.render.post.buffer;

import lombok.Getter;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;

public class SceneMRTFramebuffer extends Framebuffer {
    @Getter
    private final int sceneTextureId; // Attachment 0
    @Getter
    private final int bloomTextureId; // Attachment 1
    private final int depthRenderBufferId;

    public SceneMRTFramebuffer(int width, int height) {
        super(width, height);

        glBindFramebuffer(GL_FRAMEBUFFER, this.fboId);
        // 场景 0 场景颜色纹理
        this.sceneTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, sceneTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, sceneTextureId, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        // Bloom 1 模糊场景
        this.bloomTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, bloomTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, bloomTextureId, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        IntBuffer drawBuffers = MemoryUtil.memAllocInt(2);
        drawBuffers.put(GL_COLOR_ATTACHMENT0);
        drawBuffers.put(GL_COLOR_ATTACHMENT1);
        drawBuffers.flip();
        glDrawBuffers(drawBuffers);
        MemoryUtil.memFree(drawBuffers);

        depthRenderBufferId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBufferId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderBufferId);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("SceneMRTFramebuffer is not complete!");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, this.fboId);
        glViewport(0, 0, width, height);
    }
    @Override
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void close() throws Exception {
        super.close();
        glDeleteTextures(sceneTextureId);
        glDeleteTextures(bloomTextureId);
        glDeleteRenderbuffers(depthRenderBufferId);
    }
    @Override
    public void resize(int newWidth, int newHeight) {
        if (this.width == newWidth && this.height == newHeight) {
            return;
        }
        this.width = newWidth;
        this.height = newHeight;
        // 1. 调整 sceneTextureId 的大小
        glBindTexture(GL_TEXTURE_2D, this.sceneTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, newWidth, newHeight, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
        // 2. 调整 bloomTextureId 的大小
        glBindTexture(GL_TEXTURE_2D, this.bloomTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, newWidth, newHeight, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, this.depthRenderBufferId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, newWidth, newHeight);

        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }
}
