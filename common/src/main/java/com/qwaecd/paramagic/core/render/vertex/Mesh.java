package com.qwaecd.paramagic.core.render.vertex;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Mesh implements AutoCloseable{
    private final int vao;
    private final int vbo;
    private int vertexCount;
    private final int drawMode;
    private VertexLayout layout;

    /**
     * @param drawMode The OpenGL draw mode (e.g., GL_TRIANGLES, GL_LINES).
     */
    public Mesh(int drawMode) {
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.drawMode = drawMode;
    }

    /**
     * @param usage  OpenGL buffer usage hint (e.g., GL_STATIC_DRAW, GL_DYNAMIC_DRAW).
     */
    public void uploadData(ByteBuffer buffer, int usage) {
        if (this.layout == null) {
            throw new IllegalStateException("VertexLayout must be configured before uploading data.");
        }
        if (this.layout.getStride() == 0) {
            return;
        }
        bind();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buffer, usage);

        // buffer总字节数 / 每个顶点的总字节数(stride)
        this.vertexCount = buffer.remaining() / layout.getStride();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        unbind();
    }

    public void configure(VertexLayout layout) {
        this.layout = layout;
        bind();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        layout.apply();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        unbind();
    }

    public void bind() {
        glBindVertexArray(vao);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void draw() {
        if (vertexCount == 0) return;
        bind();
        glDrawArrays(drawMode, 0, vertexCount);
        unbind();
    }

    @Override
    public void close() throws Exception {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }
}
