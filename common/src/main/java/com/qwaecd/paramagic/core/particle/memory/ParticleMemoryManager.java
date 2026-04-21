package com.qwaecd.paramagic.core.particle.memory;

import com.qwaecd.paramagic.core.particle.ShaderBindingPoints;
import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.data.GPUParticle;
import lombok.Getter;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL43.*;

public final class ParticleMemoryManager implements AutoCloseable {
    private final EffectMetaDataMap effectMetaDataMap;
    private final int MAX_PARTICLES;
    private final int MAX_EFFECT_COUNT;
    @Getter
    private final int MAX_REQUESTS_PER_FRAME;

    private final int particleDataSSBO;
    private final int deadListSSBO;
    private final int globalData;
    private final int effectMetaDataSSBO;
    private final int requestArraySSBO;

    private final int emissionTasksSSBO;
    private final int effectPhysicsParamsSSBO;
    private final int bucketCountersSSBO;
    private final int pointBucketIndicesSSBO;
    private final int triangleBucketIndicesSSBO;
    private final int quadBucketIndicesSSBO;
    private final int bucketDrawCommandsSSBO;

    public static final int LOCAL_SIZE = 256;

    public ParticleMemoryManager(int maxParticles, int maxEffectCount) {
        this.MAX_PARTICLES = maxParticles;
        this.MAX_EFFECT_COUNT = maxEffectCount;
        this.MAX_REQUESTS_PER_FRAME = maxEffectCount * 4;
        this.particleDataSSBO = glGenBuffers();
        this.deadListSSBO = glGenBuffers();
        this.globalData = glGenBuffers();
        this.effectMetaDataSSBO = glGenBuffers();
        this.requestArraySSBO = glGenBuffers();
        this.emissionTasksSSBO = glGenBuffers();
        this.effectPhysicsParamsSSBO = glGenBuffers();
        this.bucketCountersSSBO = glGenBuffers();
        this.pointBucketIndicesSSBO = glGenBuffers();
        this.triangleBucketIndicesSSBO = glGenBuffers();
        this.quadBucketIndicesSSBO = glGenBuffers();
        this.bucketDrawCommandsSSBO = glGenBuffers();
        this.effectMetaDataMap = new EffectMetaDataMap(maxEffectCount, this.effectMetaDataSSBO);
    }

    public void unbindAllSSBO() {
        for (int i = 0; i <= ShaderBindingPoints.BUCKET_DRAW_COMMANDS; i++) {
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, i, 0);
        }
    }

    public void reserveStep() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.GLOBAL_DATA, this.globalData);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.EFFECT_META_DATA, this.effectMetaDataSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.REQUESTS, this.requestArraySSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.EMISSION_TASKS, this.emissionTasksSSBO);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.requestArraySSBO);
    }

    public void initializeParticleStep() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.GLOBAL_DATA, this.globalData);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.PARTICLE_DATA, this.particleDataSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.DEAD_LIST, this.deadListSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.REQUESTS, this.requestArraySSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.EMISSION_TASKS, this.emissionTasksSSBO);
    }

    public void physicsStep() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.GLOBAL_DATA, this.globalData);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.PARTICLE_DATA, this.particleDataSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.DEAD_LIST, this.deadListSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.EFFECT_META_DATA, this.effectMetaDataSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.EFFECT_PHYSICS_PARAMS, this.effectPhysicsParamsSSBO);
    }

    public void renderParticleStep() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.PARTICLE_DATA, this.particleDataSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.EFFECT_META_DATA, this.effectMetaDataSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.BUCKET_POINT_INDICES, this.pointBucketIndicesSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.BUCKET_TRIANGLE_INDICES, this.triangleBucketIndicesSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.BUCKET_QUAD_INDICES, this.quadBucketIndicesSSBO);
    }

    public void classifyStep() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.PARTICLE_DATA, this.particleDataSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.BUCKET_COUNTERS, this.bucketCountersSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.BUCKET_POINT_INDICES, this.pointBucketIndicesSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.BUCKET_TRIANGLE_INDICES, this.triangleBucketIndicesSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.BUCKET_QUAD_INDICES, this.quadBucketIndicesSSBO);
    }

    public void buildDrawCommandsStep() {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.BUCKET_COUNTERS, this.bucketCountersSSBO);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, ShaderBindingPoints.BUCKET_DRAW_COMMANDS, this.bucketDrawCommandsSSBO);
    }

    /**
     * 每帧分类前重置分桶计数器（point/triangle/quad）。
     */
    public void resetBucketCounters() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.bucketCountersSSBO);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer zero = stack.callocInt(4);
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, zero);
        }
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void bindPhysicsParamsBuffer() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.effectPhysicsParamsSSBO);
    }

    public void bindEffectMetaDataBuffer() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.effectMetaDataSSBO);
    }

    /**
     * 更新一个 Effect 的完整元数据。
     * 在 spawnEffect 时调用。
     *
     * @param effectId     要更新的 Effect ID。
     * @param maxParticles 该 Effect 的最大粒子数。
     * @param flags        该 Effect 的初始标志位 (e.g., EFFECT_FLAG_IS_ALIVE)。
     * @param modelMatrix  该 Effect 的初始模型矩阵
     */
    public void updateEffect(int effectId, int maxParticles, int flags, Matrix4f modelMatrix) {
        this.effectMetaDataMap.updateEffect(effectId, maxParticles, flags, modelMatrix);
    }

    /**
     * 更新一个 Effect 的标志位。
     * 在需要中断、冻结或恢复 Effect 时调用。
     * @param effectId 要更新的 Effect ID。
     * @param flags 新的标志位值。
     */
    public void updateFlags(int effectId, int flags) {
        this.effectMetaDataMap.updateFlags(effectId, flags);
    }

    /**
     * 清理一个 Effect 的元数据。
     * 在 removeEffect 时调用，将其标记为死亡。
     * @param effectId 要清理的 Effect ID。
     */
    public void clearEffect(int effectId) {
        this.effectMetaDataMap.clearEffect(effectId);
    }

    public void updateEffectModelMatrix(int effectId, Matrix4f modelMatrix) {
        this.effectMetaDataMap.updateModelMatrix(effectId, modelMatrix);
    }

    public void init() {
        initParticleDataBuffer();
        initDeadListBuffer();
        initGlobalData();
        initEffectMetaDataBuffer();
        initRequestArrayBuffer();
        initEmissionTasksBuffer();
        initEffectPhysicsParamsBuffer();
        initBucketCountersBuffer();
        initBucketIndicesBuffers();
        initBucketDrawCommandsBuffer();

        this.effectMetaDataMap.init();
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
        // 不要减一 不要减一 不要减一
        // 可用粒子栈数量而不是索引
        intBuffer.put(MAX_PARTICLES);
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
     *     uint flags;
     *     uint _padding2;
     * };
     * </pre>
     */
    private void initEffectMetaDataBuffer() {
        long bufferSizeBytes = (long) MAX_EFFECT_COUNT * EffectMetaDataMap.SIZE_IN_BYTES;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.effectMetaDataSSBO);
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
    }

    /**
     * <pre>
     * struct EffectPhysicsParams {
     *     // F(r) = A * pow(r, B) + C * pow(r, D) + E * sin(F * r + phase) * pow(r, G);
     *     vec4 primaryForce; // x: A, y: B, z: maxRadius_for_primary, w: enablePrimary(0/1)
     *     vec4 secondaryForce; // x: C, y: D, z: maxRadius_for_secondary, w: enableSecondary(0/1)
     *
     *     vec4 sinusoidalForce; // sinusoidalForce: x: E, y: F, z: G, w: enableSinus(0/1)
     *     vec4 sinusoidalExtra; // sinusoidalExtra: x: phase, z: maxRadius_for_sinusoidal, y,w = reserved
     *
     *     vec4 centerForcePos; // x, y, z: 力场中心位置, w: dragCoefficient (阻力系数), acceleration -= velocity * dragCoefficient;
     *
     *     vec4 linearForce; // x, y, z: 线性力 (e.g. gravity + wind), w: enable (0 or 1)
     * };
     * </pre>
     */
    private void initEffectPhysicsParamsBuffer() {
        long bufferSizeBytes = (long) MAX_EFFECT_COUNT * EffectPhysicsParameter.SIZE_IN_BYTES;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.effectPhysicsParamsSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, bufferSizeBytes, GL_DYNAMIC_DRAW);
    }

    private void initBucketCountersBuffer() {
        // pointCount / triangleCount / quadCount / padding
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.bucketCountersSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, Integer.BYTES * 4L, GL_DYNAMIC_DRAW);
        IntBuffer zeroBuffer = MemoryUtil.memCallocInt(4);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, zeroBuffer);
        MemoryUtil.memFree(zeroBuffer);
    }

    private void initBucketIndicesBuffers() {
        long indexBufferSize = (long) MAX_PARTICLES * Integer.BYTES;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.pointBucketIndicesSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, indexBufferSize, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.triangleBucketIndicesSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, indexBufferSize, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.quadBucketIndicesSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, indexBufferSize, GL_DYNAMIC_DRAW);
    }

    private void initBucketDrawCommandsBuffer() {
        // DrawArraysIndirectCommand = {count, instanceCount, first, baseInstance}
        // 3 commands: point / triangle / quad
        final int commandCount = 3;
        final long bytes = (long) commandCount * Integer.BYTES * 4;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.bucketDrawCommandsSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, bytes, GL_DYNAMIC_DRAW);
        IntBuffer zeroBuffer = MemoryUtil.memCallocInt(commandCount * 4);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, zeroBuffer);
        MemoryUtil.memFree(zeroBuffer);
    }

    public int getBucketDrawCommandsSSBO() {
        return this.bucketDrawCommandsSSBO;
    }

    @Override
    public void close() {
        this.effectMetaDataMap.close();
        glDeleteBuffers(this.particleDataSSBO);
        glDeleteBuffers(this.deadListSSBO);
        glDeleteBuffers(this.globalData);
        glDeleteBuffers(this.effectMetaDataSSBO);
        glDeleteBuffers(this.requestArraySSBO);
        glDeleteBuffers(this.emissionTasksSSBO);
        glDeleteBuffers(this.effectPhysicsParamsSSBO);
        glDeleteBuffers(this.bucketCountersSSBO);
        glDeleteBuffers(this.pointBucketIndicesSSBO);
        glDeleteBuffers(this.triangleBucketIndicesSSBO);
        glDeleteBuffers(this.quadBucketIndicesSSBO);
        glDeleteBuffers(this.bucketDrawCommandsSSBO);
    }
}
