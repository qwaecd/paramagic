package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.Constants;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.things.IPoseStack;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttribute;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;
import com.qwaecd.paramagic.debug.TestObj;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;

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

            material.apply();

            Shader shader = material.getShader();
            shader.setUniformMatrix4f("u_projection", projectionMatrix);
            shader.setUniformMatrix4f("u_view", view);
            shader.setUniformMatrix4f("u_model", relativeModelMatrix);

            glDisable(GL_CULL_FACE);

            renderable.getMesh().draw();
            glEnable(GL_CULL_FACE);
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

    public void test() {
        addTestObj();
    }

    private void addTestObj() {

        this.addRenderable(add());

    }

    public static TestObj add() {
        MeshBuilder meshBuilder = new MeshBuilder();
        Mesh mesh = new Mesh(GL_TRIANGLES);

        VertexLayout layout = new VertexLayout();
        layout.addAttribute(new VertexAttribute(0, 3, GL_FLOAT, false));
        layout.addAttribute(new VertexAttribute(1, 4, GL_UNSIGNED_BYTE, true));

        ByteBuffer data = meshBuilder
                // Front face
                .pos(-1.0f, -1.0f,  1.0f).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex() // Cyan
                .pos( 1.0f, -1.0f,  1.0f).color(1.0f, 0.0f, 1.0f, 1.0f).endVertex() // Magenta
                .pos( 1.0f,  1.0f,  1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex() // White
                .pos( 1.0f,  1.0f,  1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex() // White
                .pos(-1.0f,  1.0f,  1.0f).color(1.0f, 0.5f, 0.0f, 1.0f).endVertex() // Orange
                .pos(-1.0f, -1.0f,  1.0f).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex() // Cyan
                // Back face
                .pos(-1.0f, -1.0f, -1.0f).color(1.0f, 0.0f, 0.0f, 1.0f).endVertex() // Red
                .pos( 1.0f, -1.0f, -1.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex() // Green
                .pos( 1.0f,  1.0f, -1.0f).color(0.0f, 0.0f, 1.0f, 1.0f).endVertex() // Blue
                .pos( 1.0f,  1.0f, -1.0f).color(0.0f, 0.0f, 1.0f, 1.0f).endVertex() // Blue
                .pos(-1.0f,  1.0f, -1.0f).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex() // Yellow
                .pos(-1.0f, -1.0f, -1.0f).color(1.0f, 0.0f, 0.0f, 1.0f).endVertex() // Red
                // Top face
                .pos(-1.0f,  1.0f, -1.0f).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex() // Yellow
                .pos(-1.0f,  1.0f,  1.0f).color(1.0f, 0.5f, 0.0f, 1.0f).endVertex() // Orange
                .pos( 1.0f,  1.0f,  1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex() // White
                .pos( 1.0f,  1.0f,  1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex() // White
                .pos( 1.0f,  1.0f, -1.0f).color(0.0f, 0.0f, 1.0f, 1.0f).endVertex() // Blue
                .pos(-1.0f,  1.0f, -1.0f).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex() // Yellow
                // Bottom face
                .pos(-1.0f, -1.0f, -1.0f).color(1.0f, 0.0f, 0.0f, 1.0f).endVertex() // Red
                .pos( 1.0f, -1.0f, -1.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex() // Green
                .pos( 1.0f, -1.0f,  1.0f).color(1.0f, 0.0f, 1.0f, 1.0f).endVertex() // Magenta
                .pos( 1.0f, -1.0f,  1.0f).color(1.0f, 0.0f, 1.0f, 1.0f).endVertex() // Magenta
                .pos(-1.0f, -1.0f,  1.0f).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex() // Cyan
                .pos(-1.0f, -1.0f, -1.0f).color(1.0f, 0.0f, 0.0f, 1.0f).endVertex() // Red
                // Right face
                .pos( 1.0f, -1.0f, -1.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex() // Green
                .pos( 1.0f,  1.0f, -1.0f).color(0.0f, 0.0f, 1.0f, 1.0f).endVertex() // Blue
                .pos( 1.0f,  1.0f,  1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex() // White
                .pos( 1.0f,  1.0f,  1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex() // White
                .pos( 1.0f, -1.0f,  1.0f).color(1.0f, 0.0f, 1.0f, 1.0f).endVertex() // Magenta
                .pos( 1.0f, -1.0f, -1.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex() // Green
                // Left face
                .pos(-1.0f, -1.0f, -1.0f).color(1.0f, 0.0f, 0.0f, 1.0f).endVertex() // Red
                .pos(-1.0f, -1.0f,  1.0f).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex() // Cyan
                .pos(-1.0f,  1.0f,  1.0f).color(1.0f, 0.5f, 0.0f, 1.0f).endVertex() // Orange
                .pos(-1.0f,  1.0f,  1.0f).color(1.0f, 0.5f, 0.0f, 1.0f).endVertex() // Orange
                .pos(-1.0f,  1.0f, -1.0f).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex() // Yellow
                .pos(-1.0f, -1.0f, -1.0f).color(1.0f, 0.0f, 0.0f, 1.0f).endVertex() // Red
                .buildBuffer(layout);
        mesh.uploadAndConfigure(data, layout, GL_STATIC_DRAW);

        Material material = new Material(ShaderManager.getPositionColorShader());

        TestObj testObj = new TestObj(mesh, material);
        testObj.getTransform()
                .setPosition(0, 0, 0);
        return testObj;
    }
}
