package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.compute.ComputeShaderProvider;
import com.qwaecd.paramagic.core.particle.memory.ParticleMemoryManager;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.state.GLStateCache;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ParticleManager {
    public  final int MAX_PARTICLES = 1_000_000;
    public  final int MAX_EFFECT_COUNT = 64;
    private static ParticleManager INSTANCE;

    private final ParticleMemoryManager memoryManager;
    @Nullable
    private final ComputeShaderProvider shaderProvider;

    private final List<GPUParticleEffect> activeEffects;
    private final ConcurrentLinkedQueue<GPUParticleEffect> pendingAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<GPUParticleEffect> pendingRemove = new ConcurrentLinkedQueue<>();
    private final boolean canUseComputeShader;
    private final boolean canUseGeometryShader;

    private ParticleManager(boolean canUseComputeShader, boolean canUseGeometryShader) {
        this.canUseComputeShader = canUseComputeShader;
        this.canUseGeometryShader = canUseGeometryShader;
        this.memoryManager = new ParticleMemoryManager(MAX_PARTICLES, MAX_EFFECT_COUNT);
        this.activeEffects = new ArrayList<>();

        if (canUseComputeShader && canUseGeometryShader) {
            this.shaderProvider = new ComputeShaderProvider();
        } else {
            this.shaderProvider = null;
        }
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
        if (this.activeEffects.isEmpty() || shouldWork()){
            return;
        }
    }

    public void update(float deltaTime) {
        if (shouldWork() || this.shaderProvider == null) {
            return;
        }
        for (GPUParticleEffect effect : this.activeEffects) {
            effect.update(deltaTime, this.shaderProvider);
        }
    }

    private void CollectEmissionRequests() {

    }

    private boolean shouldWork() {
        return this.canUseComputeShader && this.canUseGeometryShader;
    }
}
