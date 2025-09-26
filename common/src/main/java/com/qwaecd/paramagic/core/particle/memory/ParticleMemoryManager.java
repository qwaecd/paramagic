package com.qwaecd.paramagic.core.particle.memory;

import com.qwaecd.paramagic.core.particle.ShaderBindingPoints;
import com.qwaecd.paramagic.core.particle.data.GPUParticle;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL43.*;

public final class ParticleMemoryManager implements AutoCloseable {
    private final int MAX_PARTICLES;
    private final int mainSSBO;
    private final int deadListSSBO;
    private final int atomicCounterBuffer;

    private final int LOCAL_SIZE = 256;

    public ParticleMemoryManager(int maxParticles) {
        this.MAX_PARTICLES = maxParticles;
        this.mainSSBO = glGenBuffers();
        this.deadListSSBO = glGenBuffers();
        this.atomicCounterBuffer = glGenBuffers();
    }

    public void bindBuffers() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.PARTICLE_DATA, this.mainSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.DEAD_LIST, this.deadListSSBO);
        glBindBufferBase(GL_ATOMIC_COUNTER_BUFFER, ShaderBindingPoints.ATOMIC_COUNTER, this.atomicCounterBuffer);
    }

    public void init() {
        initParticleDataBuffer();
        initDeadListBuffer();
        initAtomicCounter();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    private void initParticleDataBuffer() {
        long bufferSizeBytes = (long) MAX_PARTICLES * GPUParticle.SIZE_IN_BYTES;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.mainSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, bufferSizeBytes, GL_DYNAMIC_DRAW);
    }

    private void initDeadListBuffer() {
        long bufferSizeBytes = (long) MAX_PARTICLES * Integer.BYTES;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.deadListSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, bufferSizeBytes, GL_DYNAMIC_DRAW);

        IntBuffer intBuffer = MemoryUtil.memAllocInt(MAX_PARTICLES);
        for (int i = 0; i < MAX_PARTICLES; i++) {
            intBuffer.put(i);
        }
        intBuffer.flip();
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, intBuffer);
        MemoryUtil.memFree(intBuffer);
    }

    private void initAtomicCounter() {
        glBindBuffer(GL_ATOMIC_COUNTER_BUFFER, this.atomicCounterBuffer);
        glBufferData(GL_ATOMIC_COUNTER_BUFFER, Integer.BYTES, GL_DYNAMIC_DRAW);
        IntBuffer intBuffer = MemoryUtil.memAllocInt(1);
        intBuffer.put(0, MAX_PARTICLES);
        intBuffer.flip();
        glBufferSubData(GL_ATOMIC_COUNTER_BUFFER, 0, intBuffer);
        MemoryUtil.memFree(intBuffer);
    }

    @Override
    public void close() throws Exception {
        glDeleteBuffers(this.mainSSBO);
        glDeleteBuffers(this.deadListSSBO);
        glDeleteBuffers(this.atomicCounterBuffer);
    }
}
