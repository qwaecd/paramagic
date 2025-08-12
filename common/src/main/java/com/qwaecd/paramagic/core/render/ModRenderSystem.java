package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.Constants;
import com.qwaecd.paramagic.client.renderbase.BaseObjectManager;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.things.IPoseStack;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.*;

public class ModRenderSystem extends AbstractRenderSystem{
    private static ModRenderSystem INSTANCE;

    private final List<IRenderable> scene = new ArrayList<>();
    private final ConcurrentLinkedQueue<IRenderable> pendingAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<IRenderable> pendingRemove = new ConcurrentLinkedQueue<>();

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

    private void initialize() {
    }

    public void renderScene(RenderContext context) {
        updateScene();
        IPoseStack poseStack = context.getPoseStack();
        float timeSeconds = (System.currentTimeMillis() & 0x3fffffff) / 1000.0f;
        for (IRenderable renderable : scene) {

            Vector3d cameraPos = context.getCamera().position();
            Matrix4f worldModelMatrix = renderable.getTransform().getModelMatrix();
            Matrix4f relativeModelMatrix = new Matrix4f(worldModelMatrix);
            float relativeX = (float) (worldModelMatrix.m30() -  cameraPos.x);
            float relativeY = (float) (worldModelMatrix.m31() -  cameraPos.y);
            float relativeZ = (float) (worldModelMatrix.m32() -  cameraPos.z);
            relativeModelMatrix.setTranslation(relativeX, relativeY, relativeZ);

            Matrix4f projectionMatrix = context.getProjectionMatrix();
            Matrix4f view = poseStack.getLastPose().pose();

            Material material = renderable.getMaterial();

            renderable.getTransform()
                    .setScale(
                            (float) Math.sin(timeSeconds)*3.0f + 10.0f,
                            (float) Math.sin(timeSeconds)*3.0f + 10.0f,
                            (float) Math.sin(timeSeconds)*3.0f + 10.0f
                    )
                    .setRotation((float) Math.toRadians(90.0f), new Vector3f(1.0f, 0.0f, 0.0f));

            material.applyBaseUniforms(projectionMatrix, view, relativeModelMatrix, timeSeconds);
            material.apply();

            glDisable(GL_CULL_FACE);
            glDepthMask(false);
            glEnable(GL_BLEND);
            renderable.getMesh().draw();
            glEnable(GL_CULL_FACE);
            glDepthMask(true);
            glDisable(GL_BLEND);

            material.unbind();

        }
    }

    public void addRenderable(IRenderable renderable) {
        this.pendingAdd.add(renderable);
    }

    public void removeRenderable(IRenderable renderable) {
        this.pendingRemove.add(renderable);
    }

    public void clearAllScene() {
        this.scene.clear();
        this.pendingAdd.clear();
        this.pendingRemove.clear();
    }

    public static void initAfterClientStarted() {
        ShaderManager.init();
        BaseObjectManager.init();
        Constants.LOG.info("Render system initialized.");
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
}
