package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;

import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL33.*;

public class GPUParticleSimulator implements AutoCloseable {
    private final Shader updateShader;

    private final int[] vbos = new int[2];
    private final int[] vaosUpdate = new int[2];

    private int readIndex = 0;
    private int writeIndex = 1;

    private final int maxParticles;


    public GPUParticleSimulator(int maxParticles) {
        this.maxParticles = maxParticles;
        this.updateShader = ShaderManager.getInstance().getShaderThrowIfNotFound("particle_update");
    }

    public void update(float deltaTime, GPUParticleEffect effect) {
        int readVBO = vbos[readIndex];
        int writeVBO = vbos[writeIndex];
    }

    public void swapBuffers() {
        readIndex = 1 - readIndex;
        writeIndex = 1 - writeIndex;
    }

    public int getResultVBO() {
        return vbos[writeIndex];
    }

    @Override
    public void close() throws Exception {
        for (int i = 0; i < 2; i++) {
            glDeleteVertexArrays(vaosUpdate[i]);
            glDeleteBuffers(vbos[i]);
        }
    }
}
