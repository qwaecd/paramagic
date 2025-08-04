package com.qwaecd.paramagic.core.render.buffer;

import com.qwaecd.paramagic.platform.Services;
import lombok.experimental.UtilityClass;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

@UtilityClass
public class BufferManager {
    private int vao;
    private int vbo;

    private int prevVao;

    public void init() {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
    }

    public void bindBuffer() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
    }

    public void unbindBuffer() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void writeBuffer(FloatBuffer buffer) {
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    public void draw(int drawMod, int verts) {
        glDrawArrays(drawMod, 0, verts);
    }

    public void bind() {
        prevVao = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        glEnableVertexAttribArray(0);
        glBindVertexArray(vao);
    }

    public void unbind() {
        glBindVertexArray(prevVao);
    }
}
