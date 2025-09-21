package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.data.GPUParticle;
import com.qwaecd.paramagic.core.particle.render.ParticleMeshes;
import com.qwaecd.paramagic.core.particle.render.ParticleVAO;
import com.qwaecd.paramagic.core.particle.simulation.GPUParticleSimulator;
import com.qwaecd.paramagic.core.particle.simulation.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.memory.GPUMemoryManager;
import com.qwaecd.paramagic.core.particle.memory.ParticleBufferSlice;
import com.qwaecd.paramagic.core.particle.render.renderer.AdditiveGPUParticleRenderer;
import com.qwaecd.paramagic.core.particle.render.renderer.ParticleRenderer;
import com.qwaecd.paramagic.core.particle.render.renderer.ParticleRendererType;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.state.GLStateCache;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class ParticleManager {
    private static ParticleManager INSTANCE;

    private final List<ParticleRenderer> particleRenderers;


    private final GPUParticleSimulator particleSimulator;
    private final GPUMemoryManager gpuMemoryManager;

    private final ParticleVAO particleVAO;
    private final List<GPUParticleEffect> activeEffects;
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

        this.activeEffects = new ArrayList<>();
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
        if (this.activeEffects.isEmpty()){
            return;
        }
        for (ParticleRenderer renderer : particleRenderers) {
            renderer.render(context, stateCache);
        }
    }

    public void update(float deltaTime) {
        if (this.activeEffects.isEmpty()){
            return;
        }
        particleSimulator.update(deltaTime, this.particleVAO, this.activeEffects, this.gpuMemoryManager.getVBOId(readIndex), this.gpuMemoryManager.getVBOId(writeIndex));
        swapBuffers();
    }

    public GPUParticleEffect spawnEffect(
            int particleCount,
            Emitter emitter,
            float maxLifetime,
            ParticleRendererType rendererType
    ) {
        ParticleBufferSlice slice = gpuMemoryManager.allocate(particleCount);
        if (slice == null) {
            return null;
        }

        GPUParticleEffect effect = new GPUParticleEffect(slice, emitter, maxLifetime, rendererType);

        initializeParticleData(effect);
        activeEffects.add(effect);

        return effect;
    }

    private void initializeParticleData(GPUParticleEffect effect) {
        ParticleBufferSlice slice = effect.getSlice();
        int particleCount = slice.getParticleCount();

        int totalSizeInBytes = particleCount * GPUParticle.SIZE_IN_BYTES;
        ByteBuffer dataBuffer = MemoryUtil.memAlloc(totalSizeInBytes);

        effect.getEmitter().initialize(dataBuffer, particleCount);

        dataBuffer.flip();

        int vboId = gpuMemoryManager.getVBOId(writeIndex);
        long offsetInBytes = (long)slice.getOffset() * GPUParticle.SIZE_IN_BYTES;

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, offsetInBytes, dataBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        MemoryUtil.memFree(dataBuffer);
    }

    public int getCurrentReadVBO() {
        return this.gpuMemoryManager.getVBOId(readIndex);
    }

    private void swapBuffers() {
        readIndex = 1 - readIndex;
        writeIndex = 1 - writeIndex;
    }
}
