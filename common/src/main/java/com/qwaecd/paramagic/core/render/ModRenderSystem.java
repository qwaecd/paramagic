package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.client.renderbase.BaseObjectManager;
import com.qwaecd.paramagic.client.renderbase.factory.FullScreenQuadFactory;
import com.qwaecd.paramagic.core.particle.ParticleManager;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.post.PostProcessingManager;
import com.qwaecd.paramagic.core.render.post.buffer.FramebufferUtils;
import com.qwaecd.paramagic.core.render.post.buffer.SceneMRTFramebuffer;
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


public class ModRenderSystem extends AbstractRenderSystem{
    private static ModRenderSystem INSTANCE;

    private final RenderQueue renderQueue = new RenderQueue();
    private final GLStateCache stateCache = new GLStateCache();

    private final List<IRenderable> scene = new ArrayList<>();
    private final Set<IRenderable> sceneSet = new HashSet<>();
    private final ConcurrentLinkedQueue<IRenderable> pendingAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<IRenderable> pendingRemove = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Collection<IRenderable>> pendingBatchRemove = new ConcurrentLinkedQueue<>();

    private SceneMRTFramebuffer mainFbo;
    private PostProcessingManager postProcessingManager;
    private Mesh fullscreenQuad;

    @Getter
    private RendererManager rendererManager;
    @Getter
    private ParticleManager particleManager;
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
        BaseObjectManager.init();
        ParticleManager.init(instance.canUseComputeShader, instance.canUseGeometryShader);
        ParaConverters.init();


        instance.initializePostProcessing();
        instance.fullscreenQuad = FullScreenQuadFactory.createFullscreenQuad();
        instance.rendererManager = new RendererManager();
        instance.particleManager = ParticleManager.getInstance();
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
        // 使用真实硬件能力校验（覆盖版本启发式），避免驱动虚报或版本不代表扩展支持
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
        this.postProcessingManager = new PostProcessingManager();
        this.postProcessingManager.initialize(width, height);
    }

    public void renderScene(RenderContext context) {
        try (GLStateGuard ignored = GLStateGuard.capture()) {
            updateScene();

            renderObjectsToMainFBO(context);

            int finalSceneTexture = postProcessScene();

            blendFinalResultToMinecraft(finalSceneTexture);

            stateCache.reset();
        } finally {
            super.bindWriteMainTarget(true);
        }
    }

    private void blendFinalResultToMinecraft(int finalSceneTexture) {
        super.bindWriteMainTarget(true);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        Shader finalBlitShader = ShaderManager.getInstance().getShader("final_blit");
        finalBlitShader.bind();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, finalSceneTexture);
        finalBlitShader.setUniformValue1i("u_hdrSceneTexture", 0);
        finalBlitShader.setUniformValue1f("u_exposure", 1.0f);
        fullscreenQuad.draw();
        finalBlitShader.unbind();

        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    private int postProcessScene() {
        return postProcessingManager.process(
                mainFbo.getSceneTextureId(),
                mainFbo.getBloomTextureId()
        );
    }

    private void renderObjectsToMainFBO(RenderContext context) {
        FramebufferUtils.copyDepth(Minecraft.getInstance().getMainRenderTarget(), this.mainFbo);
        mainFbo.bind();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        // TODO: 将时间变量重构至动画系统内
        float timeSeconds = (System.currentTimeMillis() & 0x3fffffff) / 1000.0f;
        renderQueue.gather(scene, context.getCamera().position());
        renderQueue.sortForDraw();
        // 不透明（含 CUTOUT）
        stateCache.apply(RenderState.OPAQUE);
        for (RenderItem it : renderQueue.opaque) {
            drawOne(it.renderable, context, timeSeconds);
        }

        // 半透明
        stateCache.apply(RenderState.ALPHA);
        for (RenderItem it : renderQueue.transparent) {
            drawOne(it.renderable, context, timeSeconds);
        }

        // 加色发光
        stateCache.apply(RenderState.ADDITIVE);
        for (RenderItem it : renderQueue.additive) {
            drawOne(it.renderable, context, timeSeconds);
        }

        stateCache.apply(RenderState.ADDITIVE);
        this.particleManager.renderParticles(context);

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
        // TODO: 使用 UBO 优化同一帧多次设置相同的投影矩阵和视图矩阵。暂不紧急。
        material.applyBaseUniforms(projectionMatrix, view, relativeModelMatrix, timeSeconds);
        material.applyUniforms();
        renderable.getMesh().draw();

        material.unbind();
    }

    public void onWindowResize(int newWidth, int newHeight) {
        if (mainFbo != null) {
            mainFbo.resize(newWidth, newHeight);
        }
        if (postProcessingManager != null) {
            postProcessingManager.onResize(newWidth, newHeight);
        }
    }

    public void addRenderable(IRenderable renderable) {
        this.pendingAdd.add(renderable);
    }

    /**
     * this method is expensive, use with caution.<p>
     * 该方法开销很大，谨慎使用。
     * @param renderable the renderable object to remove.
     * @see #removeRenderables(Collection)
     * */
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
}
