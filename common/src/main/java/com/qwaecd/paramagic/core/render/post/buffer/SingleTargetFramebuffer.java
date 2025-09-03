package com.qwaecd.paramagic.core.render.post.buffer;

import lombok.Getter;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;

public class SingleTargetFramebuffer extends Framebuffer {
    @Getter
    private int colorTextureId;
    public SingleTargetFramebuffer(int width, int height) {
        super(width, height);

        glBindFramebuffer(GL_FRAMEBUFFER, this.fboId);

        this.colorTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, colorTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTextureId, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        // 设置 draw buffers，确保可写入颜色附件 0
        IntBuffer drawBuffers = MemoryUtil.memAllocInt(1);
        drawBuffers.put(GL_COLOR_ATTACHMENT0).flip();
        glDrawBuffers(drawBuffers);
        MemoryUtil.memFree(drawBuffers);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("SingleTargetFramebuffer is not complete!");
        }

        glBindTexture(GL_TEXTURE_2D, 0);
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
    public void resize(int newWidth, int newHeight) {
        if (this.width == newWidth && this.height == newHeight) {
            return;
        }
        this.width = newWidth;
        this.height = newHeight;
        glBindTexture(GL_TEXTURE_2D, this.colorTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, newWidth, newHeight, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public void close() throws Exception {
        super.close();
        glDeleteTextures(colorTextureId);
    }
}
