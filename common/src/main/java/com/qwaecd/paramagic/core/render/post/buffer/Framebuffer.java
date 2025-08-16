package com.qwaecd.paramagic.core.render.post.buffer;


import lombok.Getter;

import static org.lwjgl.opengl.GL33.*;

public abstract class Framebuffer implements AutoCloseable {
    @Getter
    protected final int fboId;
    @Getter
    protected int width;
    @Getter
    protected int height;

    public Framebuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.fboId = glGenFramebuffers();
    }

    public abstract void bind();
    public abstract void unbind();
    public abstract void resize(int newWidth, int newHeight);
    @Override
    public void close() throws Exception {
        glDeleteFramebuffers(fboId);
    }
}
