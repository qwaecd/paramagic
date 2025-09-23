package com.qwaecd.paramagic.core.particle.compute;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.opengl.GL46.*;

public class ComputeParticleDemo {
    private final int NUM_PARTICLES;
    private final int LOCAL_SIZE = 256; // 与 shader 中 layout(local_size_x = 256) 对齐
    private final int computeProgram;   // compute shader program id

    // GL 对象
    private int ssboPositions;
    private int ssboVelocities;
    private int vao;
    public ComputeParticleDemo() {
        this.NUM_PARTICLES = 32767;
        this.computeProgram = glCreateProgram();
    }

    public void init() {
        createSSBOs();
        createVAOForDraw();
        glUseProgram(computeProgram);
    }

    private void createSSBOs() {
        // 每个粒子用 vec4 存位置（x,y,z,w）和 vec4 存速度
        final int floatsPerParticle = 4;
        final long posBytes = (long) NUM_PARTICLES * floatsPerParticle * Float.BYTES;
        final long velBytes = (long) NUM_PARTICLES * floatsPerParticle * Float.BYTES;

        ssboPositions = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssboPositions);
        glBufferData(GL_SHADER_STORAGE_BUFFER, posBytes, GL_DYNAMIC_DRAW);

        FloatBuffer posBuffer = MemoryUtil.memAllocFloat(NUM_PARTICLES * floatsPerParticle);
        Random rand = new Random(12345);
        for (int i = 0; i < NUM_PARTICLES; i++) {
            float x = (rand.nextFloat() - 0.5f) * 10.0f;
            float y =  rand.nextFloat() * 5.0f  +  1.0f;
            float z = (rand.nextFloat() - 0.5f) * 10.0f;
            posBuffer.put(x).put(y).put(z).put(1.0f); // w = 1.0 (unused)
        }
        posBuffer.flip();
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, posBuffer);
        MemoryUtil.memFree(posBuffer);


        // 生成 SSBO（velocities）
        ssboVelocities = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssboVelocities);
        glBufferData(GL_SHADER_STORAGE_BUFFER, velBytes, GL_DYNAMIC_DRAW);

        // 初始化速度为 0
        FloatBuffer velBuffer = MemoryUtil.memAllocFloat(NUM_PARTICLES * floatsPerParticle);
        for (int i = 0; i < NUM_PARTICLES; i++) {
            velBuffer.put(0.0f).put(0.0f).put(0.0f).put(0.0f);
        }
        velBuffer.flip();
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, velBuffer);
        MemoryUtil.memFree(velBuffer);

        // 解绑
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    private void createVAOForDraw() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        // 无需设置 glEnableVertexAttribArray，因为顶点数据从 SSBO 读取（顶点着色器使用 gl_VertexID）
        glBindVertexArray(0);
    }

}
