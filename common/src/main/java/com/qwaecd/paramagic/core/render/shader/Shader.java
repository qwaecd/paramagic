package com.qwaecd.paramagic.core.render.shader;

import lombok.Getter;

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

    public void uniformMatrix4f(String name, FloatBuffer matrix) {
        bind();
        glUniformMatrix4fv(glGetUniformLocation(programId, name), false, matrix);
        unbind();
    }

    public void uniformValue2f(String name, float v0, float v1) {
        bind();
        glUniform2f(glGetUniformLocation(programId, name), v0, v1);
        unbind();
    }
}
