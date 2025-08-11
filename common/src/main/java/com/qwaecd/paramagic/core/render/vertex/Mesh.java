package com.qwaecd.paramagic.core.render.vertex;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Mesh implements AutoCloseable{
    private final int vao;
    private final int vbo;
    private int ebo;
    private int indexCount;
    private int indexType;
    private int vertexCount;
    private final int drawMode;

    /**
     * @param drawMode The OpenGL draw mode (e.g., GL_TRIANGLES, GL_LINES).
     */
    public Mesh(int drawMode) {
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.ebo = 0;
        this.drawMode = drawMode;
    }

    /**
     * @param usage  OpenGL buffer usage hint (e.g., GL_STATIC_DRAW, GL_DYNAMIC_DRAW).
     */
    public void uploadAndConfigure(ByteBuffer buffer, VertexLayout layout, int usage) {
        if (layout == null || layout.getStride() == 0) {
            this.vertexCount = 0;
            this.indexCount = 0;
            return;
        }

        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);

        glBufferData(GL_ARRAY_BUFFER, buffer, usage);
        layout.apply();
        // buffer总字节数 / 每个顶点的总字节数(stride)
        this.vertexCount = buffer.remaining() / layout.getStride();
        this.indexCount = 0;

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * U16 indexes
     */
    public void uploadAndConfigure(ByteBuffer vertexBuffer,
                                   VertexLayout layout,
                                   int vertexUsage,
                                   ShortBuffer indexBuffer,
                                   int indexUsage) {
        uploadAndConfigureInternal(vertexBuffer, layout, vertexUsage, indexBuffer, GL_UNSIGNED_SHORT, indexUsage);
    }

    /**
     * U32 indexes
     */
    public void uploadAndConfigure(ByteBuffer vertexBuffer,
                                   VertexLayout layout,
                                   int vertexUsage,
                                   IntBuffer indexBuffer,
                                   int indexUsage) {
        uploadAndConfigureInternal(vertexBuffer, layout, vertexUsage, indexBuffer, GL_UNSIGNED_INT, indexUsage);
    }

    private void uploadAndConfigureInternal(ByteBuffer vertexBuffer,
                                            VertexLayout layout,
                                            int vertexUsage,
                                            Buffer typedIndexBuffer,
                                            int indexType,
                                            int indexUsage) {
        if (layout == null || layout.getStride() == 0) {
            this.vertexCount = 0;
            this.indexCount = 0;
            return;
        }

        glBindVertexArray(this.vao);

        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, vertexUsage);
        layout.apply();
        this.vertexCount = vertexBuffer.remaining() / layout.getStride();

        if (this.ebo == 0) {
            this.ebo = glGenBuffers();
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        if (indexType == GL_UNSIGNED_SHORT) {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, (ShortBuffer) typedIndexBuffer, indexUsage);
            this.indexCount = ((ShortBuffer) typedIndexBuffer).limit();
        } else {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) typedIndexBuffer, indexUsage);
            this.indexCount = ((IntBuffer) typedIndexBuffer).limit();
        }
        this.indexType = indexType;

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void draw() {
        if (indexCount == 0 && vertexCount == 0) return;
        glBindVertexArray(this.vao);
        if (this.ebo != 0 && this.indexCount > 0) {
            glDrawElements(this.drawMode, this.indexCount, this.indexType, 0L);
        } else {
            glDrawArrays(this.drawMode, 0, this.vertexCount);
        }
        glBindVertexArray(0);
    }

    @Override
    public void close() throws Exception {
        if (ebo != 0) glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }
}
