package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.compute.CShaderProvider;
import com.qwaecd.paramagic.core.particle.compute.ComputeShader;
import com.qwaecd.paramagic.core.particle.compute.IComputeShaderProvider;
import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.effect.EffectFlags;
import com.qwaecd.paramagic.core.particle.effect.EffectManager;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.memory.ParticleMemoryManager;
import com.qwaecd.paramagic.core.particle.request.ParticleEmissionProcessor;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL40.glDrawArraysIndirect;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BARRIER_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

@PlatformScope(PlatformScopeType.CLIENT)
public class ParticleSystem implements AutoCloseable {
    public  final int MAX_PARTICLES = 1_000_000;
    public  final int MAX_EFFECT_COUNT = 128;
    private static ParticleSystem INSTANCE;

    private final ParticleMemoryManager memoryManager;
    private final EffectManager effectManager;
    private final ParticleEmissionProcessor emissionProcessor;
    private final IComputeShaderProvider computeShaderProvider;
    @Nullable
    private final Shader pointRenderShader;
    @Nullable
    private final Shader shapeRenderShader;

    private final boolean canUseComputeShader;
    private final boolean canUseGeometryShader;

    // 用于重用的暂存发射请求的列表
    private final List<EmissionRequest> emissionRequests;
    private final ConcurrentLinkedQueue<GPUParticleEffect> killedEffects = new ConcurrentLinkedQueue<>();
    private final int emptyVao;
    private static final int BUCKET_TYPE_POINT = 0;
    private static final int BUCKET_TYPE_TRIANGLE = 1;
    private static final int BUCKET_TYPE_QUAD = 2;
    private static final int INDIRECT_COMMAND_STRIDE_BYTES = 4 * Integer.BYTES; // DrawArraysIndirectCommand

    // debug data
    private static final int DEBUG_STATS_SIZE_BYTES = 4 * Integer.BYTES;
    private static final int DEBUG_STATS_FREE_COUNT_OFFSET = 0;
    private static final int DEBUG_STATS_SUCCESSFUL_TASK_COUNT_OFFSET = Integer.BYTES;
    private final long[] debugStatsFences = new long[ParticleMemoryManager.DEBUG_STATS_BUFFER_COUNT];
    private final ByteBuffer debugStatsReadbackBuffer = BufferUtils.createByteBuffer(DEBUG_STATS_SIZE_BYTES).order(ByteOrder.nativeOrder());
    private boolean debugStatsEnabled;
    private int debugStatsFrameIndex;
    private int cachedAliveParticleCount;
    private int cachedFreeParticleCount;
    private int cachedSuccessfulTaskCount;

    private ParticleSystem(boolean canUseComputeShader, boolean canUseGeometryShader) {
        this.canUseComputeShader = canUseComputeShader;
        this.canUseGeometryShader = canUseGeometryShader;
        this.memoryManager = new ParticleMemoryManager(MAX_PARTICLES, MAX_EFFECT_COUNT);
        this.effectManager = new EffectManager(MAX_EFFECT_COUNT, this.memoryManager);

        this.computeShaderProvider = new CShaderProvider(this.canUseComputeShader && this.canUseGeometryShader);
        this.emissionProcessor = new ParticleEmissionProcessor(computeShaderProvider, this.memoryManager.getMAX_REQUESTS_PER_FRAME());

        this.pointRenderShader = ShaderManager.getInstance().getShaderNullable("particle_render_point");
        this.shapeRenderShader = ShaderManager.getInstance().getShaderNullable("particle_render_shape");
        this.cachedFreeParticleCount = MAX_PARTICLES;

        this.emissionRequests = new ArrayList<>(MAX_EFFECT_COUNT);
        this.emptyVao = glGenVertexArrays();
        glEnable(GL_PROGRAM_POINT_SIZE);
    }

    public static ParticleSystem getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ParticleManager has not been initialized.");
        }
        return INSTANCE;
    }

    public void reset() {
        this.effectManager.reset();
        this.emissionRequests.clear();
        this.killedEffects.clear();
    }

    public static boolean isInitialized() {
        return INSTANCE != null;
    }

    public static void init(boolean canUseComputeShader, boolean canUseGeometryShader) {
        if (INSTANCE != null) {
            Paramagic.LOG.warn("ParticleManager is already initialized.");
            return;
        }
        INSTANCE = new ParticleSystem(canUseComputeShader, canUseGeometryShader);
        if (canUseComputeShader && canUseGeometryShader) {
            INSTANCE.memoryManager.init();
        } else {
            Paramagic.LOG.warn("Compute shaders are not supported. Particle effects will be disabled.");
        }
    }

    public int getActiveEffectCount() {
        return this.effectManager.getCurrentEffectCount();
    }

    public void renderParticles(RenderContext context) {
        if (!shouldWork()) {
            return;
        }

        if (this.pointRenderShader == null) {
            return;
        }

        if (this.effectManager.getCurrentEffectCount() == 0) {
            this.tryWriteDebugStats();
            return;
        }

        this.dispatchClassify();
        if (!this.dispatchBuildDrawCommands()) {
            return;
        }
        this.effectManager.forEachActiveEffect(this::uploadEffectModelMatrixIfDirty);

        Matrix4f projectionMatrix = context.getProjectionMatrix();
        Matrix4f viewMatrix = context.getMatrixStackProvider().getViewMatrix();
        Vector3d cameraPos = context.getCamera().position();
        this.memoryManager.renderParticleStep();
        glBindVertexArray(this.emptyVao);
        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, this.memoryManager.getBucketDrawCommandsSSBO());

        // Point bucket
        this.pointRenderShader.bind();
        this.setCommonRenderUniforms(this.pointRenderShader, projectionMatrix, viewMatrix, cameraPos, this.computePointSizeScale(projectionMatrix));
        this.pointRenderShader.setUniformValue1i("u_bucketType", BUCKET_TYPE_POINT);
        glDrawArraysIndirect(GL_POINTS, 0L);
        this.pointRenderShader.unbind();

        // Triangle + Quad buckets share one geometry shader, selected by uniform branch.
        if (this.shapeRenderShader != null) {
            this.shapeRenderShader.bind();
            this.setCommonRenderUniforms(this.shapeRenderShader, projectionMatrix, viewMatrix, cameraPos, this.computePointSizeScale(projectionMatrix));

            this.shapeRenderShader.setUniformValue1i("u_bucketType", BUCKET_TYPE_TRIANGLE);
            glDrawArraysIndirect(GL_POINTS, (long) INDIRECT_COMMAND_STRIDE_BYTES);

            this.shapeRenderShader.setUniformValue1i("u_bucketType", BUCKET_TYPE_QUAD);
            glDrawArraysIndirect(GL_POINTS, (long) INDIRECT_COMMAND_STRIDE_BYTES * 2L);
            this.shapeRenderShader.unbind();
        }

        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0);
        glBindVertexArray(0);
        glUseProgram(0);
        this.memoryManager.unbindAllSSBO();

        this.tryWriteDebugStats();
    }

    private void tryWriteDebugStats() {
        if (this.debugStatsEnabled) {
            this.dispatchDebugStatsWrite();
            this.pollDebugStatsReadback();
        }
    }

    public void update(float deltaTime) {
        flushEffects();
        if (this.effectManager.getCurrentEffectCount() == 0 || !this.shouldWork()) {
            return;
        }

        this.emissionRequests.clear();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer singleEffectBuffer = stack.malloc(EffectPhysicsParameter.SIZE_IN_BYTES).order(ByteOrder.nativeOrder());
            this.memoryManager.bindPhysicsParamsBuffer();
            this.effectManager.forEachActiveEffect(activeEffect -> {
                // 更新 effect 内部状态
                activeEffect.update(deltaTime);

                if (!activeEffect.isAlive()) {
                    activeEffect.setEffectFlag(EffectFlags.KILL_ALL.get());
                    this.killedEffects.add(activeEffect);
                    return;
                }

                // 收集发射请求
                this.collectEmissionRequests(activeEffect);
                // 上传物理参数至 GPU 等待后续 update 使用
                this.uploadPhysicsParams(activeEffect, singleEffectBuffer);
            });
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        }
        this.processRequests();
        this.dispatchUpdate(deltaTime);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean spawnEffect(GPUParticleEffect effect) {
        if (!shouldWork()) {
            return false;
        }
        return this.effectManager.spawnEffect(effect);
    }

    public void removeEffect(GPUParticleEffect effect) {
        this.effectManager.removeEffect(effect);
    }

    public void setDebugStatsEnabled(boolean enabled) {
        if (this.debugStatsEnabled == enabled) {
            return;
        }
        this.debugStatsEnabled = enabled;
        if (!enabled) {
            this.deleteAllDebugStatsFences();
        }
    }

    public boolean isDebugStatsEnabled() {
        return this.debugStatsEnabled;
    }

    public int getDebugAliveParticleCount() {
        return this.cachedAliveParticleCount;
    }

    public int getDebugFreeParticleCount() {
        return this.cachedFreeParticleCount;
    }

    public int getDebugSuccessfulTaskCount() {
        return this.cachedSuccessfulTaskCount;
    }

    @Override
    public void close() {
        this.deleteAllDebugStatsFences();
        this.memoryManager.close();
        if (INSTANCE == this) {
            INSTANCE = null;
        }
    }

    private void dispatchUpdate(float deltaTime) {
        this.memoryManager.physicsStep();

        ComputeShader updateShader = this.computeShaderProvider.particleUpdateShader();
        updateShader.bind();
        updateShader.setUniformValue1i("u_maxParticles", MAX_PARTICLES);
        updateShader.setUniformValue1f("u_deltaTime", deltaTime);
        updateShader.setUniformValue1i("u_maxEffectCount", MAX_EFFECT_COUNT);
        updateShader.dispatch((MAX_PARTICLES + ParticleMemoryManager.LOCAL_SIZE - 1) / ParticleMemoryManager.LOCAL_SIZE, 1, 1);

        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        updateShader.unbind();
        memoryManager.unbindAllSSBO();
    }

    private void dispatchClassify() {
        ComputeShader classifyShader = this.computeShaderProvider.particleClassifyShader();
        if (classifyShader == null) {
            return;
        }
        this.memoryManager.resetBucketCounters();
        this.memoryManager.classifyStep();

        classifyShader.bind();
        classifyShader.setUniformValue1i("u_maxParticles", MAX_PARTICLES);
        classifyShader.dispatch((MAX_PARTICLES + ParticleMemoryManager.LOCAL_SIZE - 1) / ParticleMemoryManager.LOCAL_SIZE, 1, 1);

        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        classifyShader.unbind();
        this.memoryManager.unbindAllSSBO();
    }

    private boolean dispatchBuildDrawCommands() {
        ComputeShader commandShader = this.computeShaderProvider.particleBuildDrawCommandsShader();
        if (commandShader == null) {
            return false;
        }
        this.memoryManager.buildDrawCommandsStep();
        commandShader.bind();
        commandShader.dispatch(1, 1, 1);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT | GL_COMMAND_BARRIER_BIT);
        commandShader.unbind();
        this.memoryManager.unbindAllSSBO();
        return true;
    }

    private void dispatchDebugStatsWrite() {
        ComputeShader debugStatsShader = this.computeShaderProvider.particleDebugStatsShader();
        if (debugStatsShader == null) {
            return;
        }
        int writeIndex = this.debugStatsFrameIndex % ParticleMemoryManager.DEBUG_STATS_BUFFER_COUNT;
        this.deleteDebugStatsFence(writeIndex);

        this.memoryManager.debugStatsWriteStep(writeIndex);
        debugStatsShader.bind();
        debugStatsShader.dispatch(1, 1, 1);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT | GL_BUFFER_UPDATE_BARRIER_BIT);
        debugStatsShader.unbind();
        this.memoryManager.unbindAllSSBO();

        this.debugStatsFences[writeIndex] = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
    }

    private void pollDebugStatsReadback() {
        int readIndex = (this.debugStatsFrameIndex + 1) % ParticleMemoryManager.DEBUG_STATS_BUFFER_COUNT;
        long fence = this.debugStatsFences[readIndex];
        if (fence != 0L) {
            int result = glClientWaitSync(fence, 0, 0);
            if (result == GL_ALREADY_SIGNALED || result == GL_CONDITION_SATISFIED) {
                this.readDebugStatsBuffer(readIndex);
                this.deleteDebugStatsFence(readIndex);
            }
        }
        this.debugStatsFrameIndex++;
    }

    private void readDebugStatsBuffer(int bufferIndex) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.memoryManager.getDebugStatsSSBO(bufferIndex));
        this.debugStatsReadbackBuffer.clear();
        glGetBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, this.debugStatsReadbackBuffer);
        this.cachedFreeParticleCount = this.debugStatsReadbackBuffer.getInt(DEBUG_STATS_FREE_COUNT_OFFSET);
        this.cachedSuccessfulTaskCount = this.debugStatsReadbackBuffer.getInt(DEBUG_STATS_SUCCESSFUL_TASK_COUNT_OFFSET);
        this.cachedAliveParticleCount = MAX_PARTICLES - this.cachedFreeParticleCount;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    private void deleteDebugStatsFence(int index) {
        long fence = this.debugStatsFences[index];
        if (fence != 0L) {
            glDeleteSync(fence);
            this.debugStatsFences[index] = 0L;
        }
    }

    private void deleteAllDebugStatsFences() {
        for (int i = 0; i < ParticleMemoryManager.DEBUG_STATS_BUFFER_COUNT; i++) {
            this.deleteDebugStatsFence(i);
        }
    }

    private void setCommonRenderUniforms(Shader shader, Matrix4f projectionMatrix, Matrix4f viewMatrix, Vector3d cameraPos, float pointSizeScale) {
        shader.setUniformMatrix4f("u_projectionMatrix", projectionMatrix);
        shader.setUniformMatrix4f("u_viewMatrix", viewMatrix);
        shader.setUniformValue3f("u_cameraPosition", (float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
        shader.setUniformValue1f("u_pointSizeScale", pointSizeScale);
    }

    private float computePointSizeScale(Matrix4f projectionMatrix) {
        int viewportHeight = Minecraft.getInstance().getWindow().getHeight();
        return 0.5f * viewportHeight * projectionMatrix.m11();
    }

    private void uploadPhysicsParams(GPUParticleEffect activeEffect, ByteBuffer singleEffectBuffer) {
        EffectPhysicsParameter param = activeEffect.getPhysicsParameter();
        if (param.isDirty()) {
            int effectId = activeEffect.getEffectId();
            if (effectId < 0 || effectId >= MAX_EFFECT_COUNT) {
                return;
            }
            long offset = (long)effectId * EffectPhysicsParameter.SIZE_IN_BYTES;
            singleEffectBuffer.clear();
            param.writePhysicsParamsToBuffer(singleEffectBuffer);
            singleEffectBuffer.flip();
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, offset, singleEffectBuffer);
            param.setDirty(false);
        }
    }

    private void uploadEffectModelMatrixIfDirty(GPUParticleEffect activeEffect) {
        Transform transform = activeEffect.getTransform();
        if (!transform.isDirty()) {
            return;
        }
        int effectId = activeEffect.getEffectId();
        if (effectId < 0 || effectId >= MAX_EFFECT_COUNT) {
            return;
        }
        this.memoryManager.updateEffectModelMatrix(effectId, transform.getModelMatrix());
    }

    private void collectEmissionRequests(GPUParticleEffect effect) {
        List<EmissionRequest> requestsFromEffect = effect.getEmissionRequests();
        if (!requestsFromEffect.isEmpty()) {
            this.emissionRequests.addAll(requestsFromEffect);
        }
    }

    private void processRequests() {
        final int requestCount = Math.min(this.emissionRequests.size(), this.memoryManager.getMAX_REQUESTS_PER_FRAME());

        this.emissionProcessor.reserveParticles(requestCount, this.emissionRequests, this.memoryManager);
        this.emissionProcessor.initializeParticles(requestCount, this.memoryManager);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean shouldWork() {
        return this.canUseComputeShader && this.canUseGeometryShader;
    }

    private void flushEffects() {
        GPUParticleEffect effect;
        while ((effect = this.killedEffects.poll()) != null) {
            this.removeEffect(effect);
        }
    }
}
