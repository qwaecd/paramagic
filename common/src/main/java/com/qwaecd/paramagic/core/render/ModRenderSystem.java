package com.qwaecd.paramagic.core.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.qwaecd.paramagic.Constants;
import com.qwaecd.paramagic.client.renderbase.BaseObjectManager;
import com.qwaecd.paramagic.client.renderbase.factory.FullScreenQuadFactory;
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
import com.qwaecd.paramagic.core.render.things.IPoseStack;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL33.*;


public class ModRenderSystem extends AbstractRenderSystem{
    private static ModRenderSystem INSTANCE;

    private final RenderQueue renderQueue = new RenderQueue();
    private final GLStateCache stateCache = new GLStateCache();

    private final List<IRenderable> scene = new ArrayList<>();
    private final ConcurrentLinkedQueue<IRenderable> pendingAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<IRenderable> pendingRemove = new ConcurrentLinkedQueue<>();

    private SceneMRTFramebuffer mainFbo;
    private PostProcessingManager postProcessingManager;
    private Mesh fullscreenQuad;

    private final Matrix4f reusableMatrix = new Matrix4f();

    private ModRenderSystem() {
        Constants.LOG.info("ModRenderSystem instance created.");
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
        ShaderManager.init();
        BaseObjectManager.init();
        ModRenderSystem.getInstance().initializePostProcessing();
        ModRenderSystem.getInstance().fullscreenQuad = FullScreenQuadFactory.createFullscreenQuad();
        Constants.LOG.info("Render system initialized.");
    }

    private void initializePostProcessing() {
        int width = Minecraft.getInstance().getWindow().getWidth();
        int height = Minecraft.getInstance().getWindow().getHeight();

        this.mainFbo = new SceneMRTFramebuffer(width, height);
        this.postProcessingManager = new PostProcessingManager();
        this.postProcessingManager.initialize(width, height);
    }

    public void renderScene(RenderContext context) {
        Minecraft mc = Minecraft.getInstance();
        RenderTarget mainRenderTarget = mc.getMainRenderTarget();
        try (GLStateGuard ignored = GLStateGuard.capture()) {
            mainRenderTarget.bindWrite(false);
            updateScene();

            renderObjectsToMainFBO(context);

            int finalSceneTexture = postProcessScene();

            blendFinalResultToMinecraft(mainRenderTarget, finalSceneTexture);

            stateCache.reset();
        } finally {
            mainRenderTarget.bindWrite(true);
        }
    }

    private void blendFinalResultToMinecraft(RenderTarget mainRenderTarget, int finalSceneTexture) {
        mainRenderTarget.bindWrite(true);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        Shader finalBlitShader = ShaderManager.getShader("final_blit");
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

        mainFbo.unbind();
    }


    private void drawOne(IRenderable renderable, RenderContext context, float timeSeconds) {
        IPoseStack poseStack = context.getPoseStack();

        Vector3d cameraPos = context.getCamera().position();
        Matrix4f worldModelMatrix = renderable.getTransform().getModelMatrix();
        Matrix4f relativeModelMatrix = reusableMatrix.set(worldModelMatrix);
        float relativeX = (float) (worldModelMatrix.m30() -  cameraPos.x);
        float relativeY = (float) (worldModelMatrix.m31() -  cameraPos.y);
        float relativeZ = (float) (worldModelMatrix.m32() -  cameraPos.z);
        relativeModelMatrix.setTranslation(relativeX, relativeY, relativeZ);

        Matrix4f projectionMatrix = context.getProjectionMatrix();
        Matrix4f view = poseStack.getLastPose().pose();

        AbstractMaterial material = renderable.getMaterial();

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

    public void removeRenderable(IRenderable renderable) {
        this.pendingRemove.add(renderable);
    }

    public void clearAll() {
        this.scene.clear();
        this.pendingAdd.clear();
        this.pendingRemove.clear();
        this.renderQueue.clear();
    }

    private void updateScene() {
        IRenderable obj;
        while ((obj = pendingAdd.poll()) != null) {
            scene.add(obj);
        }
        while ((obj = pendingRemove.poll()) != null) {
            scene.remove(obj);
        }
    }

    public static boolean isInitialized() {
        return INSTANCE != null;
    }
}
