package com.qwaecd.paramagic.core.particle.compute;

import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.StringIntMap;
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

    private final StringIntMap uniformLocationCache = new StringIntMap(32);

    private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);


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

    /** 返回 location（int），内部会缓存并在未命中时查询 glGetUniformLocation */
    private int loc(String uniformName) {
        int v = uniformLocationCache.get(uniformName);
        if (v != StringIntMap.MISSING) return v;
        int location = glGetUniformLocation(programId, uniformName);
        uniformLocationCache.put(uniformName, location);
        return location;
    }

    public void setUniformMatrix4f(String name, FloatBuffer matrix) {
        int l = loc(name);
        glUniformMatrix4fv(l, false, matrix);
    }

    public void setUniformMatrix4f(String name, Matrix4f matrix) {
        // 复用 matrixBuffer，避免每次分配
        matrix.get(matrixBuffer);
//        matrixBuffer.flip(); It is not necessary because get() already sets the position to 0 and limit to 16
        int l = loc(name);
        glUniformMatrix4fv(l, false, matrixBuffer);
//        setUniformMatrix4f(name, matrix.get(BufferUtils.createFloatBuffer(16)));
    }

    public void setUniformValue3f(String name, float v0, float v1, float v2) {
        int l = loc(name);
        glUniform3f(l, v0, v1, v2);
    }

    public void setUniformValue3f(String name, Vector3f v) {
        setUniformValue3f(name, v.x, v.y, v.z);
    }

    public void setUniformValue4f(String name, float v0, float v1, float v2, float v3) {
        int l = loc(name);
        glUniform4f(l, v0, v1, v2, v3);
    }

    public void setUniformValue4f(String name, Vector4f v) {
        setUniformValue4f(name, v.x, v.y, v.z, v.w);
    }

    public void setUniformValue2f(String name, float v0, float v1) {
        int l = loc(name);
        glUniform2f(l, v0, v1);
    }

    public void setUniformValue1f(String name, float value) {
        int l = loc(name);
        glUniform1f(l, value);
    }

    public void setUniformValue1i(String name, int value) {
        int l = loc(name);
        glUniform1i(l, value);
    }
}
