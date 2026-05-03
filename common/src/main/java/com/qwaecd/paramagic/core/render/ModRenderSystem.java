package com.qwaecd.paramagic.core.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.client.renderbase.SharedMeshes;
import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.geometricmask.GeometricEffectCaster;
import com.qwaecd.paramagic.core.render.geometricmask.GeometricMaskSceneTextures;
import com.qwaecd.paramagic.core.render.post.FinalComposePass;
import com.qwaecd.paramagic.core.render.post.PostProcessSceneTextures;
import com.qwaecd.paramagic.core.render.post.PostProcessingManager;
import com.qwaecd.paramagic.core.render.post.ScreenSpaceEffectManager;
import com.qwaecd.paramagic.core.render.post.buffer.*;
import com.qwaecd.paramagic.core.render.queue.RenderItem;
import com.qwaecd.paramagic.core.render.queue.RenderQueue;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.state.GLStateCache;
import com.qwaecd.paramagic.core.render.state.GLStateGuard;
import com.qwaecd.paramagic.core.render.state.RenderState;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.things.IMatrixStackProvider;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.data.para.converter.ParaConverters;
import com.qwaecd.paramagic.tools.shader.ShaderCapabilityChecker;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL33.*;


public class ModRenderSystem extends AbstractRenderSystem implements AutoCloseable {
    private static ModRenderSystem INSTANCE;

    private final RenderQueue renderQueue = new RenderQueue();
    private final GLStateCache stateCache = new GLStateCache();

    private final List<IRenderable> scene = new ArrayList<>();
    private final Set<IRenderable> sceneSet = new HashSet<>();
    private final ConcurrentLinkedQueue<IRenderable> pendingAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<IRenderable> pendingRemove = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Collection<IRenderable>> pendingBatchRemove = new ConcurrentLinkedQueue<>();

    private final List<GeometricEffectCaster> geometricEffectCasters = new ArrayList<>();
    private final Set<GeometricEffectCaster> geometricCasterSet = new HashSet<>();
    private final ConcurrentLinkedQueue<GeometricEffectCaster> pendingGeometricAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<GeometricEffectCaster> pendingGeometricRemove = new ConcurrentLinkedQueue<>();

    private SceneMRTFramebuffer mainFbo;
    private SceneCopyFBO sceneCopyFBO;
    /**
     * 几何遮罩主路径：mesh 写入 mask 纹理（见 {@link com.qwaecd.paramagic.core.render.geometricmask}）
     */
    private ColorDepthFramebuffer geometricMaskFbo;
    private SingleTargetFramebuffer combinedSceneFbo;
    private PostProcessingManager postProcessingManager;
    private FinalComposePass finalComposePass;
    private ScreenSpaceEffectManager screenSpaceEffectManager;
    private Mesh fullscreenQuad;
    private Shader presentShader;

    @Getter
    private RendererManager rendererManager;
    @Getter
    private ParticleSystem particleSystem;
    private boolean canUseComputeShader = false;
    private boolean canUseGeometryShader = false;

    private final Matrix4f reusableMatrix = new Matrix4f();

    private ModRenderSystem() {
        Paramagic.LOG.info("ModRenderSystem instance created.");
    }

    public static ModRenderSystem getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ModRenderSystem has not been initialized yet!");
        }
        return INSTANCE;
    }
    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new ModRenderSystem();
            INSTANCE.initialize();
        }
    }

    public void initialize() {
    }

    public static void initAfterClientStarted() {
        ModRenderSystem instance = ModRenderSystem.getInstance();
        instance.checkGLVersion();

        ShaderManager.init();
        SharedMeshes.init();
        ParticleSystem.init(instance.canUseComputeShader, instance.canUseGeometryShader);
        ParaConverters.init();


        instance.initializePostProcessing();
        instance.fullscreenQuad = SharedMeshes.fullscreenQuad();
        instance.rendererManager = new RendererManager();
        instance.particleSystem = ParticleSystem.getInstance();
        Paramagic.LOG.info("Render system initialized.");
    }

    private void checkGLVersion() {
        String s = glGetString(GL_VERSION);
        Paramagic.LOG.info("OpenGL version: {}", s);
        if (s != null && !s.isEmpty()) {
            String[] parts = s.split(" ");
            String versionPart = parts[0];
            String[] versionNumbers = versionPart.split("\\.");
            try {
                int major = Integer.parseInt(versionNumbers[0]);
                int minor = Integer.parseInt(versionNumbers[1]);
                float version = major + minor / 10.0f;
                if (version < 3.2f) {
                    Paramagic.LOG.warn("OpenGL version is lower than 3.2. Some features may not work correctly.");
                }
                if (version >= 3.2f) {
                    this.canUseGeometryShader = true; // provisional, will be validated by capability checker
                }
                if (version >= 4.3f) {
                    this.canUseComputeShader = true; // provisional
                }
            } catch (Exception ignored) {
            }
        }
        ShaderCapabilityChecker.CapabilityReport report = ShaderCapabilityChecker.detect();
        boolean oldGeom = this.canUseGeometryShader;
        boolean oldComp = this.canUseComputeShader;
        this.canUseGeometryShader = report.geometrySupported;
        this.canUseComputeShader = report.computeSupported;
        if (oldGeom != this.canUseGeometryShader) {
            Paramagic.LOG.info("Geometry shader capability adjusted: versionHeuristic={} -> realCapability={} ({})", oldGeom, this.canUseGeometryShader, report.geometryReason);
        } else {
            Paramagic.LOG.info("Geometry shader capability confirmed: {} ({})", this.canUseGeometryShader, report.geometryReason);
        }
        if (oldComp != this.canUseComputeShader) {
            Paramagic.LOG.info("Compute shader capability adjusted: versionHeuristic={} -> realCapability={} ({})", oldComp, this.canUseComputeShader, report.computeReason);
        } else {
            Paramagic.LOG.info("Compute shader capability confirmed: {} ({})", this.canUseComputeShader, report.computeReason);
        }
    }

    private void initializePostProcessing() {
        int width = Minecraft.getInstance().getWindow().getWidth();
        int height = Minecraft.getInstance().getWindow().getHeight();

        this.mainFbo = new SceneMRTFramebuffer(width, height);
        this.sceneCopyFBO = new SceneCopyFBO(width, height);
        this.geometricMaskFbo = new ColorDepthFramebuffer(width, height);
        this.combinedSceneFbo = new SingleTargetFramebuffer(width, height);
        this.postProcessingManager = new PostProcessingManager();
        this.postProcessingManager.initialize(width, height);
        this.finalComposePass = new FinalComposePass();
        this.finalComposePass.initialize();
        this.screenSpaceEffectManager = new ScreenSpaceEffectManager(geometricMaskFbo);
        this.screenSpaceEffectManager.initialize(width, height);
        this.presentShader = ShaderManager.getInstance().getShaderThrowIfNotFound("bloom_composite");
    }

    public void renderScene(RenderContext context) {
        try (GLStateGuard ignored = GLStateGuard.capture()) {
            updateScene();
            updateGeometricCasters();

            renderObjectsToMainFBO(context);

            PostProcessSceneTextures postTextures = postProcessingManager.processSceneTextures(
                    mainFbo.getSceneTextureId(),
                    mainFbo.getBloomTextureId()
            );
            int hdrSceneTexture = postTextures.hdrModCompositeTextureId();
            int combinedSceneTexture = composeFinalScene(hdrSceneTexture);
            GeometricMaskSceneTextures geometricInputs = new GeometricMaskSceneTextures(
                    combinedSceneTexture,
                    postTextures.hdrModCompositeTextureId(),
                    postTextures.blurredBloomTextureId(),
                    sceneCopyFBO.getGameSceneTextureId()
            );
            float timeSeconds = (System.currentTimeMillis() & 0x3fffffff) / 1000.0f;
            int finalSceneTexture = screenSpaceEffectManager.applyGeometricMaskEffects(
                    context,
                    timeSeconds,
                    geometricInputs,
                    Minecraft.getInstance().getMainRenderTarget(),
                    geometricEffectCasters
            );

            presentFinalResultToMinecraft(finalSceneTexture);

            stateCache.reset();
        } finally {
            super.bindWriteMainTarget(true);
        }
    }

    private int composeFinalScene(int hdrSceneTexture) {
        combinedSceneFbo.bind();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        finalComposePass.combine(hdrSceneTexture, this.sceneCopyFBO.getGameSceneTextureId(), 1.0f, false);
        combinedSceneFbo.unbind();
        return combinedSceneFbo.getColorTextureId();
    }

    private void presentFinalResultToMinecraft(int finalSceneTexture) {
        super.bindWriteMainTarget(true);

        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        presentShader.bind();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, finalSceneTexture);
        presentShader.setUniformValue1i("u_texture", 0);
        fullscreenQuad.draw();
        presentShader.unbind();

        glDepthMask(true);
    }

    private void renderObjectsToMainFBO(RenderContext context) {
        RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
        FramebufferUtils.copyDepth(mainRenderTarget, this.mainFbo);
        FramebufferUtils.copy(mainRenderTarget, this.sceneCopyFBO, GL_COLOR_BUFFER_BIT);
        mainFbo.bind();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        float timeSeconds = (System.currentTimeMillis() & 0x3fffffff) / 1000.0f;
        renderQueue.gather(scene, context.getCamera().position());
        renderQueue.sortForDraw();
        stateCache.apply(RenderState.OPAQUE);
        for (RenderItem it : renderQueue.opaque) {
            drawOne(it.renderable, context, timeSeconds);
        }

        stateCache.apply(RenderState.ALPHA);
        for (RenderItem it : renderQueue.transparent) {
            drawOne(it.renderable, context, timeSeconds);
        }

        stateCache.apply(RenderState.ADDITIVE);
        for (RenderItem it : renderQueue.additive) {
            drawOne(it.renderable, context, timeSeconds);
        }

        stateCache.apply(RenderState.ADDITIVE);
        this.particleSystem.renderParticles(context);

        mainFbo.unbind();
    }


    private void drawOne(IRenderable renderable, RenderContext context, float timeSeconds) {
        IMatrixStackProvider matrixProvider = context.getMatrixStackProvider();

        Vector3d cameraPos = context.getCamera().position();
        Matrix4f worldModelMatrix = renderable.getPrecomputedWorldTransform()
                .orElseGet(
                () -> renderable.getTransform().getModelMatrix()
            );

        Matrix4f relativeModelMatrix = reusableMatrix.set(worldModelMatrix);
        float relativeX = (float) (worldModelMatrix.m30() - cameraPos.x);
        float relativeY = (float) (worldModelMatrix.m31() - cameraPos.y);
        float relativeZ = (float) (worldModelMatrix.m32() - cameraPos.z);
        relativeModelMatrix.setTranslation(relativeX, relativeY, relativeZ);

        Matrix4f projectionMatrix = context.getProjectionMatrix();
        Matrix4f view = matrixProvider.getViewMatrix();

        AbstractMaterial material = renderable.getMaterial();
        material.applyBaseUniforms(projectionMatrix, view, relativeModelMatrix, timeSeconds);
        material.applyUniforms();
        renderable.getMesh().draw();

        material.unbind();
    }

    public void onWindowResize(int newWidth, int newHeight) {
        if (this.mainFbo != null) {
            this.mainFbo.resize(newWidth, newHeight);
        }
        if (this.geometricMaskFbo != null) {
            this.geometricMaskFbo.resize(newWidth, newHeight);
        }
        if (this.postProcessingManager != null) {
            this.postProcessingManager.onResize(newWidth, newHeight);
        }
        if (this.sceneCopyFBO != null) {
            this.sceneCopyFBO.resize(newWidth, newHeight);
        }
        if (this.combinedSceneFbo != null) {
            this.combinedSceneFbo.resize(newWidth, newHeight);
        }
        if (this.screenSpaceEffectManager != null) {
            this.screenSpaceEffectManager.onResize(newWidth, newHeight);
        }
    }

    public void addRenderable(IRenderable renderable) {
        this.pendingAdd.add(renderable);
    }

    /**
     * 注册几何遮罩发射体（不参与 {@link com.qwaecd.paramagic.core.render.queue.RenderQueue}）。
     */
    public void addGeometricEffectCaster(GeometricEffectCaster caster) {
        this.pendingGeometricAdd.add(caster);
    }

    public void removeGeometricEffectCaster(GeometricEffectCaster caster) {
        this.pendingGeometricRemove.add(caster);
    }

    @Deprecated(forRemoval = false)
    public void removeRenderable(IRenderable renderable) {
        this.pendingRemove.add(renderable);
    }

    public void removeRenderables(Collection<IRenderable> renderables) {
        if (renderables != null && !renderables.isEmpty()) {
            this.pendingBatchRemove.add(renderables);
        }
    }

    public void clearAll() {
        this.scene.clear();
        this.sceneSet.clear();
        this.pendingAdd.clear();
        this.pendingRemove.clear();
        this.pendingBatchRemove.clear();
        this.renderQueue.clear();
        this.geometricEffectCasters.clear();
        this.geometricCasterSet.clear();
        this.pendingGeometricAdd.clear();
        this.pendingGeometricRemove.clear();
    }

    private void updateScene() {
        IRenderable obj;
        while ((obj = pendingAdd.poll()) != null) {
            if (sceneSet.add(obj)) {
                scene.add(obj);
            }
        }
        Collection<IRenderable> batchToRemove;
        while ((batchToRemove = pendingBatchRemove.poll()) != null) {
            sceneSet.removeAll(batchToRemove);
            scene.removeAll(batchToRemove);
        }

        while ((obj = pendingRemove.poll()) != null) {
            scene.remove(obj);
        }
    }

    private void updateGeometricCasters() {
        GeometricEffectCaster c;
        while ((c = pendingGeometricAdd.poll()) != null) {
            if (geometricCasterSet.add(c)) {
                geometricEffectCasters.add(c);
            }
        }
        while ((c = pendingGeometricRemove.poll()) != null) {
            geometricCasterSet.remove(c);
            geometricEffectCasters.remove(c);
        }
    }

    @SuppressWarnings("all")
    public boolean canUseComputerShader() {
        return this.canUseComputeShader;
    }

    @SuppressWarnings("all")
    public boolean canUseGeometryShader() {
        return this.canUseGeometryShader;
    }

    public static boolean isInitialized() {
        return INSTANCE != null;
    }

    @Override
    public void close() {
        closeQuietly("postProcessingManager", this.postProcessingManager);
        closeQuietly("finalComposePass", this.finalComposePass);
        closeQuietly("screenSpaceEffectManager", this.screenSpaceEffectManager);
        closeQuietly("mainFbo", this.mainFbo);
        closeQuietly("sceneCopyFBO", this.sceneCopyFBO);
        closeQuietly("geometricMaskFbo", this.geometricMaskFbo);
        closeQuietly("combinedSceneFbo", this.combinedSceneFbo);
        try {
            SharedMeshes.close();
        } catch (Exception e) {
            Paramagic.LOG.warn("Failed to close shared meshes.", e);
        }

        this.postProcessingManager = null;
        this.finalComposePass = null;
        this.screenSpaceEffectManager = null;
        this.mainFbo = null;
        this.sceneCopyFBO = null;
        this.geometricMaskFbo = null;
        this.combinedSceneFbo = null;
        this.fullscreenQuad = null;
        INSTANCE = null;
    }

    private static void closeQuietly(String resourceName, AutoCloseable resource) {
        if (resource == null) {
            return;
        }
        try {
            resource.close();
        } catch (Exception e) {
            Paramagic.LOG.warn("Failed to close render resource: {}", resourceName, e);
        }
    }
}
