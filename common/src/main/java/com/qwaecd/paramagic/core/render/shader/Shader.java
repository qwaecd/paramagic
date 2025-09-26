package com.qwaecd.paramagic.core.render.shader;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Shader {
    @Getter
    private final String name;
    @Getter
    private final String path;
    @Getter
    private final int programId;
    /**
     * @param path 着色器文件路径，相对于 resources 下的 shaders 目录，shaders下的传空字符串
     * @param name 着色器名称，文件名（不带扩展名）
     */
    public Shader(String path, String name, int programId) {
        this.name = name;
        this.path = path;
        this.programId = programId;
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

    public void setUniformValue3f(String name, Vector3f v) {
        glUniform3f(glGetUniformLocation(programId, name), v.x, v.y, v.z);
    }

    public void setUniformValue4f(String name, float v0, float v1, float v2, float v3) {
        glUniform4f(glGetUniformLocation(programId, name), v0, v1, v2, v3);
    }

    public void setUniformValue4f(String name, Vector4f v) {
        glUniform4f(glGetUniformLocation(programId, name), v.x, v.y, v.z, v.w);
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

    @Override
    public String toString() {
        return "Shader{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", programId=" + programId +
                '}';
    }
}
