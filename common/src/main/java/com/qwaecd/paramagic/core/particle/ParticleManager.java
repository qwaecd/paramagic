package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.memory.GPUMemoryManager;
import com.qwaecd.paramagic.core.particle.memory.ParticleBufferSlice;
import com.qwaecd.paramagic.core.particle.renderer.AdditiveGPUParticleRenderer;
import com.qwaecd.paramagic.core.particle.renderer.ParticleRenderer;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.state.GLStateCache;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager {
    private static ParticleManager INSTANCE;

    private final List<ParticleRenderer> particleRenderers;


    private final GPUParticleSimulator particleSimulator;
    private final GPUMemoryManager gpuMemoryManager;

    private final ParticleVAO particleVAO;
    private final List<ParticleBufferSlice> activeSlices;
    private int readIndex = 0;
    private int writeIndex = 1;
    /**
     * 期望的粒子数量最大值，大小最终会被向上调整为2的幂次方，以适应内存管理器的分配策略。
     */
    public final int maxParticles = (int) Math.pow(2,18);   // 2^18 = 262144


    private ParticleManager() {
        this.gpuMemoryManager = new GPUMemoryManager(this.maxParticles);
        this.particleSimulator = new GPUParticleSimulator();

        this.particleRenderers = new ArrayList<>(1);
        particleRenderers.add(new AdditiveGPUParticleRenderer());

        this.activeSlices = new ArrayList<>();
        this.particleVAO = new ParticleVAO();
    }

    public static ParticleManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ParticleManager has not been initialized.");
        }
        return INSTANCE;
    }

    public static void init() {
        if (INSTANCE != null) {
            Paramagic.LOG.warn("ParticleManager is already initialized.");
            return;
        }
        INSTANCE = new ParticleManager();
        ParticleMeshes.init();
    }

    public void renderParticles(RenderContext context, GLStateCache stateCache) {
        if (this.activeSlices.isEmpty()){
            return;
        }
        for (ParticleRenderer renderer : particleRenderers) {
            renderer.render(context, stateCache);
        }
    }

    public void update(float deltaTime) {
        if (this.activeSlices.isEmpty()){
            return;
        }
        particleSimulator.update(deltaTime, this.particleVAO, this.activeSlices, this.gpuMemoryManager.getVBOId(readIndex), this.gpuMemoryManager.getVBOId(writeIndex));
        swapBuffers();
    }

    public int getCurrentReadVBO() {
        return this.gpuMemoryManager.getVBOId(readIndex);
    }

    private void swapBuffers() {
        readIndex = 1 - readIndex;
        writeIndex = 1 - writeIndex;
    }
}
