package com.qwaecd.paramagic.core.particle.memory;

import com.qwaecd.paramagic.core.particle.ShaderBindingPoints;
import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.data.GPUParticle;
import lombok.Getter;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL43.*;

public final class ParticleMemoryManager implements AutoCloseable {
    private final int MAX_PARTICLES;
    private final int MAX_EFFECT_COUNT;
    @Getter
    private final int MAX_REQUESTS_PER_FRAME;

    private final int particleDataSSBO;
    private final int deadListSSBO;
    private final int globalData;
    private final int effectCountersListSSBO;
    private final int requestArraySSBO;

    private final int emissionTasksSSBO;
    private final int effectPhysicsParamsSSBO;

    @Getter
    private static final int LOCAL_SIZE = 256;

    public ParticleMemoryManager(int maxParticles, int maxEffectCount) {
        this.MAX_PARTICLES = maxParticles;
        this.MAX_EFFECT_COUNT = maxEffectCount;
        this.MAX_REQUESTS_PER_FRAME = maxEffectCount * 4;
        this.particleDataSSBO = glGenBuffers();
        this.deadListSSBO = glGenBuffers();
        this.globalData = glGenBuffers();
        this.effectCountersListSSBO = glGenBuffers();
        this.requestArraySSBO = glGenBuffers();
        this.emissionTasksSSBO = glGenBuffers();
        this.effectPhysicsParamsSSBO = glGenBuffers();
    }

    public void bindMainBuffers() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.GLOBAL_DATA, this.globalData);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.PARTICLE_DATA, this.particleDataSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.DEAD_LIST, this.deadListSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.EFFECT_COUNTERS, this.effectCountersListSSBO);
    }

    public void reserveStep() {
        bindMainBuffers();
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.REQUESTS, this.requestArraySSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.EMISSION_TASKS, this.emissionTasksSSBO);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.requestArraySSBO);
    }

    public void initializeParticleStep() {
        bindMainBuffers();
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.REQUESTS, this.requestArraySSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.EMISSION_TASKS, this.emissionTasksSSBO);
    }

    public void physicsStep() {
        bindMainBuffers();
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.EFFECT_PHYSICS_PARAMS, this.effectPhysicsParamsSSBO);
    }

    public void renderParticleStep() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.PARTICLE_DATA, this.particleDataSSBO);
    }

    public void init() {
        initParticleDataBuffer();
        initDeadListBuffer();
        initGlobalData();
        initEffectCountersListBuffer();
        initRequestArrayBuffer();
        initEmissionTasksBuffer();
        initEffectPhysicsParamsBuffer();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    private void initParticleDataBuffer() {
        long bufferSizeBytes = (long) MAX_PARTICLES * GPUParticle.SIZE_IN_BYTES;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.particleDataSSBO);
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

    /**
     * <pre>
     * struct GlobalCounters {
     *     uint deadListStackTop;
     *     uint successfulTaskCount;
     *     uint _padding2;
     *     uint _padding3;
     * };
     * </pre>
     */
    private void initGlobalData() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.globalData);
        glBufferData(GL_SHADER_STORAGE_BUFFER, Integer.BYTES * 4, GL_DYNAMIC_DRAW);
        IntBuffer intBuffer = MemoryUtil.memAllocInt(4);
        intBuffer.put(MAX_PARTICLES - 1);
        intBuffer.put(0);
        intBuffer.put(0);
        intBuffer.put(0);
        intBuffer.flip();
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, intBuffer);
        MemoryUtil.memFree(intBuffer);
    }

    /**
     * <pre>
     * struct EffectMetaData {
     *     uint maxParticles;
     *     uint currentCount;
     *     uint _padding1;
     *     uint _padding2;
     * };
     * </pre>
     */
    private void initEffectCountersListBuffer() {
        long bufferSizeBytes = (long) MAX_EFFECT_COUNT * Integer.BYTES * 4;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.effectCountersListSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, bufferSizeBytes, GL_DYNAMIC_DRAW);
        IntBuffer zeroBuffer = MemoryUtil.memCallocInt(MAX_EFFECT_COUNT * 4);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, zeroBuffer);
        MemoryUtil.memFree(zeroBuffer);
    }

    private void initRequestArrayBuffer() {
        long bufferSizeBytes = (long) MAX_REQUESTS_PER_FRAME * EmissionRequest.SIZE_IN_BYTES;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.requestArraySSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, bufferSizeBytes, GL_DYNAMIC_DRAW);
    }

    /**
     * <pre>
     * struct EmissionTask {
     *     uint numParticlesToInit;
     *     uint indexStackOffset;
     *     uint _padding0;
     *     uint _padding1;
     *     EmissionRequest request;
     * };
     * </pre>
     */
    private void initEmissionTasksBuffer() {
        long bufferSizeBytes = (long) MAX_REQUESTS_PER_FRAME * EmissionRequest.SIZE_IN_BYTES + Integer.BYTES * 4;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.emissionTasksSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, bufferSizeBytes, GL_DYNAMIC_DRAW);
        IntBuffer zeroBuffer = MemoryUtil.memCallocInt(1);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, zeroBuffer);
        MemoryUtil.memFree(zeroBuffer);
    }

    /**
     * <pre>
     * struct EffectPhysicsParams {
     *     // F(r) = A * pow(r, B)
     *     vec4 centerForceParams; // x: A, y: B, z: maxRadius, w: enable (0 or 1)
     *     vec4 centerForcePos; // x, y, z: 力场中心位置, w: dragCoefficient (阻力系数), acceleration -= velocity * dragCoefficient;
     *     vec4 linearForce; // x, y, z: 线性力 (e.g. gravity + wind), w: enable (0 or 1)
     * };</pre>
     */
    private void initEffectPhysicsParamsBuffer() {
        long bufferSizeBytes = (long) MAX_EFFECT_COUNT * EffectPhysicsParameter.SIZE_IN_BYTES;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.effectPhysicsParamsSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, bufferSizeBytes, GL_DYNAMIC_DRAW);
    }

    @Override
    public void close() {
        glDeleteBuffers(this.particleDataSSBO);
        glDeleteBuffers(this.deadListSSBO);
        glDeleteBuffers(this.globalData);
        glDeleteBuffers(this.effectCountersListSSBO);
        glDeleteBuffers(this.requestArraySSBO);
        glDeleteBuffers(this.emissionTasksSSBO);
        glDeleteBuffers(this.effectPhysicsParamsSSBO);
    }
}
