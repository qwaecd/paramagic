package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.data.ParticleMeshes;
import com.qwaecd.paramagic.core.particle.simulation.GPUParticleSimulator;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.state.GLStateCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ParticleManager {
    private static ParticleManager INSTANCE;

    private final GPUParticleSimulator particleSimulator;

    private final List<GPUParticleEffect> activeEffects;
    private final ConcurrentLinkedQueue<GPUParticleEffect> pendingAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<GPUParticleEffect> pendingRemove = new ConcurrentLinkedQueue<>();
    private final boolean canUseComputeShader;
    /**
     * 期望的粒子数量最大值，大小最终会被向上调整为2的幂次方，以适应内存管理器的分配策略。
     */
    public final int maxParticles = (int) Math.pow(2, 18);   // 2^18 = 262144


    private ParticleManager(boolean canUseComputeShader) {
        this.canUseComputeShader = canUseComputeShader;
        this.particleSimulator = new GPUParticleSimulator();

        this.activeEffects = new ArrayList<>();
    }

    public static ParticleManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ParticleManager has not been initialized.");
        }
        return INSTANCE;
    }

    public static void init(boolean canUseComputeShader) {
        if (INSTANCE != null) {
            Paramagic.LOG.warn("ParticleManager is already initialized.");
            return;
        }
        INSTANCE = new ParticleManager(canUseComputeShader);
        ParticleMeshes.init();
    }

    public void renderParticles(RenderContext context, GLStateCache stateCache) {
        if (this.activeEffects.isEmpty() || !this.canUseComputeShader){
            return;
        }
    }

    public void update(float deltaTime) {
        if (!this.canUseComputeShader) {
            return;
        }
    }
}
