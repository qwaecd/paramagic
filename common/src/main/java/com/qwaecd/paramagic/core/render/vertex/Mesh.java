package com.qwaecd.paramagic.core.render.vertex;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Mesh implements AutoCloseable{
    private final int vao;
    private final int vbo;
    private int vertexCount;
    private final int drawMode;

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
    public void uploadAndConfigure(ByteBuffer buffer, VertexLayout layout, int usage) {
        if (layout == null || layout.getStride() == 0) {
            this.vertexCount = 0;
            return;
        }

        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);

        glBufferData(GL_ARRAY_BUFFER, buffer, usage);
        layout.apply();
        // buffer总字节数 / 每个顶点的总字节数(stride)
        this.vertexCount = buffer.remaining() / layout.getStride();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void draw() {
        if (vertexCount == 0) return;
        glBindVertexArray(this.vao);
        glDrawArrays(drawMode, 0, vertexCount);
        glBindVertexArray(0);
    }

    @Override
    public void close() throws Exception {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }
}
