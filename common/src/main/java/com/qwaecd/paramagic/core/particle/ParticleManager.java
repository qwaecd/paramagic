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
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BARRIER_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class ParticleManager {
    public  final int MAX_PARTICLES = 1_000_000;
    public  final int MAX_EFFECT_COUNT = 64;
    private static ParticleManager INSTANCE;

    private final ParticleMemoryManager memoryManager;
    private final EffectManager effectManager;
    private final ParticleEmissionProcessor emissionProcessor;
    private final IComputeShaderProvider computeShaderProvider;
    @Nullable
    private final Shader renderShader;

    private final boolean canUseComputeShader;
    private final boolean canUseGeometryShader;

    // 用于重用的暂存发射请求的列表
    private final List<EmissionRequest> emissionRequests;
    private final ConcurrentLinkedQueue<GPUParticleEffect> killedEffects = new ConcurrentLinkedQueue<>();
    private final int emptyVao;

    private ParticleManager(boolean canUseComputeShader, boolean canUseGeometryShader) {
        this.canUseComputeShader = canUseComputeShader;
        this.canUseGeometryShader = canUseGeometryShader;
        this.memoryManager = new ParticleMemoryManager(MAX_PARTICLES, MAX_EFFECT_COUNT);
        this.effectManager = new EffectManager(MAX_EFFECT_COUNT, this.memoryManager);

        this.computeShaderProvider = new CShaderProvider(this.canUseComputeShader && this.canUseGeometryShader);
        this.emissionProcessor = new ParticleEmissionProcessor(computeShaderProvider, this.memoryManager.getMAX_REQUESTS_PER_FRAME());

        this.renderShader = ShaderManager.getInstance().getShaderNullable("particle_render");

        this.emissionRequests = new ArrayList<>(MAX_EFFECT_COUNT);
        this.emptyVao = glGenVertexArrays();
        glEnable(GL_PROGRAM_POINT_SIZE);
    }

    public static ParticleManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ParticleManager has not been initialized.");
        }
        return INSTANCE;
    }

    public static void init(boolean canUseComputeShader, boolean canUseGeometryShader) {
        if (INSTANCE != null) {
            Paramagic.LOG.warn("ParticleManager is already initialized.");
            return;
        }
        INSTANCE = new ParticleManager(canUseComputeShader, canUseGeometryShader);
        if (canUseComputeShader && canUseGeometryShader) {
            INSTANCE.memoryManager.init();
        } else {
            Paramagic.LOG.warn("Compute shaders are not supported. Particle effects will be disabled.");
        }
    }

    public void renderParticles(RenderContext context) {
        if (this.effectManager.getCurrentEffectCount() == 0 || !shouldWork() || this.renderShader == null){
            return;
        }

        Matrix4f projectionMatrix = context.getProjectionMatrix();
        Matrix4f viewMatrix = context.getMatrixStackProvider().getViewMatrix();
        Vector3d cameraPos = context.getCamera().position();
        // Pass uniforms to particle_render.vsh
        this.memoryManager.renderParticleStep();
        glBindVertexArray(this.emptyVao);
        this.renderShader.bind();
        this.renderShader.setUniformMatrix4f("u_projectionMatrix", projectionMatrix);
        this.renderShader.setUniformMatrix4f("u_viewMatrix", viewMatrix);
        this.renderShader.setUniformValue3f("u_cameraPosition", (float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);

        glDrawArrays(GL_POINTS, 0, MAX_PARTICLES);
        glBindVertexArray(0);
        glUseProgram(0);
    }

    public void update(float deltaTime) {
        flushEffects();
        if (this.effectManager.getCurrentEffectCount() == 0 || !shouldWork()) {
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
                collectEmissionRequests(activeEffect);
                // 上传物理参数至 GPU 等待后续 update 使用
                uploadPhysicsParams(activeEffect, singleEffectBuffer);
            });
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        }
        processRequests();
        dispatchUpdate(deltaTime);
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
