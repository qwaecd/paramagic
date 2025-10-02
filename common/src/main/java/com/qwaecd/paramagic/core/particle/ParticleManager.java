package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.compute.CShaderProvider;
import com.qwaecd.paramagic.core.particle.compute.IComputeShaderProvider;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.memory.ParticleMemoryManager;
import com.qwaecd.paramagic.core.particle.request.ParticleEmissionProcessor;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.state.GLStateCache;
import com.qwaecd.paramagic.core.render.state.RenderState;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;

public class ParticleManager {
    public  final int MAX_PARTICLES = 1_000_000;
    public  final int MAX_EFFECT_COUNT = 64;
    private static ParticleManager INSTANCE;

    private final ParticleMemoryManager memoryManager;
    private final ParticleEmissionProcessor emissionProcessor;
    private final IComputeShaderProvider shaderProvider;
    @Nullable
    private final Shader renderShader;

    private final List<GPUParticleEffect> activeEffects;
    private final ConcurrentLinkedQueue<GPUParticleEffect> pendingAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<GPUParticleEffect> pendingRemove = new ConcurrentLinkedQueue<>();
    private final boolean canUseComputeShader;
    private final boolean canUseGeometryShader;

    // 用于重用的暂存发射请求的列表
    private final List<EmissionRequest> emissionRequests;
    private final int emptyVao;

    private ParticleManager(boolean canUseComputeShader, boolean canUseGeometryShader) {
        this.canUseComputeShader = canUseComputeShader;
        this.canUseGeometryShader = canUseGeometryShader;
        this.memoryManager = new ParticleMemoryManager(MAX_PARTICLES, MAX_EFFECT_COUNT);
        this.activeEffects = new ArrayList<>();

        this.shaderProvider = new CShaderProvider(this.canUseComputeShader && this.canUseGeometryShader);
        this.emissionProcessor = new ParticleEmissionProcessor(shaderProvider, this.memoryManager.getMAX_REQUESTS_PER_FRAME());

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

    public void renderParticles(RenderContext context, GLStateCache stateCache) {
        if (this.activeEffects.isEmpty() || !shouldWork() || this.renderShader == null){
            return;
        }
        stateCache.apply(RenderState.ADDITIVE);
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
        flushPendingEffects();
        if (this.activeEffects.isEmpty() || !shouldWork()) {
            return;
        }
        collectEmissionRequests();
        processRequests();

        updateActiveEffects(deltaTime);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean spawnEffect(GPUParticleEffect effect) {
        if (!shouldWork()) {
            return false;
        }
        return this.pendingAdd.offer(effect);
    }

    public void flushPendingEffects() {
        GPUParticleEffect effect;
        while ((effect = this.pendingAdd.poll()) != null) {
            if (this.activeEffects.size() < MAX_EFFECT_COUNT) {
                this.activeEffects.add(effect);
            } else {
                Paramagic.LOG.warn("Maximum number of active particle effects reached. Cannot add more effects.");
                break;
            }
        }
        while ((effect = this.pendingRemove.poll()) != null) {
            this.activeEffects.remove(effect);
        }
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
        final int requestCount = Math.min(this.emissionRequests.size(), this.memoryManager.getMAX_REQUESTS_PER_FRAME());

        this.emissionProcessor.reserveParticles(requestCount, this.emissionRequests, this.memoryManager);
        this.emissionProcessor.initializeParticles(requestCount, this.memoryManager);
    }

    private boolean shouldWork() {
        return this.canUseComputeShader && this.canUseGeometryShader;
    }
}
