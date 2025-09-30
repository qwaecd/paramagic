package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.compute.CShaderProvider;
import com.qwaecd.paramagic.core.particle.compute.IComputeShaderProvider;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.memory.ParticleMemoryManager;
import com.qwaecd.paramagic.core.particle.request.ParticleEmissionProcessor;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.state.GLStateCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ParticleManager {
    public  final int MAX_PARTICLES = 1_000_000;
    public  final int MAX_EFFECT_COUNT = 64;
    private static ParticleManager INSTANCE;

    private final ParticleMemoryManager memoryManager;
    private final ParticleEmissionProcessor emissionProcessor;
    private final IComputeShaderProvider shaderProvider;

    private final List<GPUParticleEffect> activeEffects;
    private final ConcurrentLinkedQueue<GPUParticleEffect> pendingAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<GPUParticleEffect> pendingRemove = new ConcurrentLinkedQueue<>();
    private final boolean canUseComputeShader;
    private final boolean canUseGeometryShader;

    // 用于重用的暂存发射请求的列表
    private final List<EmissionRequest> emissionRequests;

    private ParticleManager(boolean canUseComputeShader, boolean canUseGeometryShader) {
        this.canUseComputeShader = canUseComputeShader;
        this.canUseGeometryShader = canUseGeometryShader;
        this.memoryManager = new ParticleMemoryManager(MAX_PARTICLES, MAX_EFFECT_COUNT);
        this.activeEffects = new ArrayList<>();

        this.shaderProvider = new CShaderProvider(this.canUseComputeShader && this.canUseGeometryShader);
        this.emissionProcessor = new ParticleEmissionProcessor(shaderProvider, this.memoryManager.getMAX_REQUESTS_PER_FRAME());

        this.emissionRequests = new ArrayList<>(MAX_EFFECT_COUNT);
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

    public void renderParticles(RenderContext context, GLStateCache stateCache) {
        if (this.activeEffects.isEmpty() || !shouldWork()){
            return;
        }
    }

    public void update(float deltaTime) {
        if (this.activeEffects.isEmpty() || !shouldWork()) {
            return;
        }
        // TODO: 还需处理当前帧新增以及移除的effect
        collectEmissionRequests();
        processRequests();

        updateActiveEffects(deltaTime);
    }

    private void updateActiveEffects(float deltaTime) {
        for (GPUParticleEffect effect : this.activeEffects) {
            effect.update(deltaTime, this.shaderProvider);
        }
    }

    private void collectEmissionRequests() {
        this.emissionRequests.clear();
        for (GPUParticleEffect effect : this.activeEffects) {
            List<EmissionRequest> requestsFromEffect = effect.getEmissionRequests();
            if (!requestsFromEffect.isEmpty()) {
                this.emissionRequests.addAll(requestsFromEffect);
            }
        }
    }

    private void processRequests() {
        this.emissionProcessor.reserveParticles(this.emissionRequests, this.memoryManager);
    }

    private boolean shouldWork() {
        return this.canUseComputeShader && this.canUseGeometryShader;
    }
}
