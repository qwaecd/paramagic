package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.memory.ParticleMemoryManager;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.state.GLStateCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ParticleManager {
    public  static final int MAX_PARTICLES = 1_000_000;
    private static ParticleManager INSTANCE;

    private final ParticleMemoryManager memoryManager;

    private final List<GPUParticleEffect> activeEffects;
    private final ConcurrentLinkedQueue<GPUParticleEffect> pendingAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<GPUParticleEffect> pendingRemove = new ConcurrentLinkedQueue<>();
    private final boolean canUseComputeShader;
    private final boolean canUseGeometryShader;

    private ParticleManager(boolean canUseComputeShader, boolean canUseGeometryShader) {
        this.canUseComputeShader = canUseComputeShader;
        this.canUseGeometryShader = canUseGeometryShader;
        this.memoryManager = new ParticleMemoryManager(MAX_PARTICLES);
        this.activeEffects = new ArrayList<>();
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
        if (shouldWork()) {
            return;
        }
    }

    private boolean shouldWork() {
        return this.canUseComputeShader && this.canUseGeometryShader;
    }
}
