package com.qwaecd.paramagic.core.render.shader;

import lombok.Getter;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Shader {
    @Getter
    private final int programId;

    public Shader(String name) {
        int v = ShaderManager.loadShaderProgram(name, ShaderManager.ShaderType.VERTEX);
        int f = ShaderManager.loadShaderProgram(name, ShaderManager.ShaderType.FRAGMENT);
        this.programId = glCreateProgram();
        glAttachShader(programId, v);
        glAttachShader(programId, f);
        glLinkProgram(programId);
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void setUniformMatrix4f(String name, FloatBuffer matrix) {
        glUniformMatrix4fv(glGetUniformLocation(programId, name), false, matrix);
    }

    public void setUniformMatrix4f(String name, Matrix4f matrix) {
        setUniformMatrix4f(name, matrix.get(BufferUtils.createFloatBuffer(16)));
    }

    public void setUniformValue3f(String name, float v0, float v1, float v2) {
        glUniform3f(glGetUniformLocation(programId, name), v0, v1, v2);
    }

    public void setUniformValue4f(String name, float v0, float v1, float v2, float v3) {
        glUniform4f(glGetUniformLocation(programId, name), v0, v1, v2, v3);
    }

    public void setUniformValue2f(String name, float v0, float v1) {
        glUniform2f(glGetUniformLocation(programId, name), v0, v1);
    }

    public void setUniformValue1f(String name, float value) {
        glUniform1f(glGetUniformLocation(programId, name), value);
    }

    public void setUniformValue1i(String name, int value) {
        glUniform1i(glGetUniformLocation(programId, name), value);
    }
}
