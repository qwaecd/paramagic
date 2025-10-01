package com.qwaecd.paramagic.core.particle.compute;

import com.qwaecd.paramagic.core.render.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

public class ComputeShader {
    private final Shader shader;
    private final int programId;
    private final Map<String, Integer> subroutineMap;

    public ComputeShader(Shader shader) {
        this.subroutineMap = new HashMap<>();
        this.shader = shader;
        this.programId = shader.getProgramId();
        loadSubroutines();
    }

    private void loadSubroutines() {
        // TODO: 加载所有的子函数缓存
        // glGetSubroutineIndex(int program, int shadertype(GL_COMPUTE_SHADER), String name)
    }

    public int getSubroutineIndex(String subroutineName) {
        return subroutineMap.getOrDefault(subroutineName, -1);
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void dispatch(int numGroupsX, int numGroupsY, int numGroupsZ) {
        glDispatchCompute(numGroupsX, numGroupsY, numGroupsZ);
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
}
