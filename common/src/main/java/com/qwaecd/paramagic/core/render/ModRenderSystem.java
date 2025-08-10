package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.Constants;
import com.qwaecd.paramagic.core.render.buffer.BufferManager;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.things.ICamera;
import com.qwaecd.paramagic.core.render.things.IPoseStack;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttribute;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;
import com.qwaecd.paramagic.debug.TestObj;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

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


            Material material = renderable.getMaterial();

            material.apply();

            Shader shader = material.getShader();
            Matrix4f projectionMatrix = context.getProjectionMatrix();
            Matrix4f view = poseStack.getLastPose().pose();
            Matrix4f modelMatrix = renderable.getTransform().getModelMatrix();
            shader.uniformMatrix4f("u_projection", projectionMatrix);
            shader.uniformMatrix4f("u_view", view);
            shader.uniformMatrix4f("u_model", modelMatrix);
            renderable.getMesh().draw();
            material.unbind();

        }
    }

    public void addRenderable(IRenderable renderable) {
        this.pendingAdd.add(renderable);
    }

    public void removeRenderable(IRenderable renderable) {
        this.pendingRemove.add(renderable);
    }

    public static void initAfterClientStarted() {
        BufferManager.init();
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
        MeshBuilder meshBuilder = new MeshBuilder();
        Mesh mesh = new Mesh(GL_TRIANGLES);

        VertexLayout layout = new VertexLayout();
        layout.addAttribute(new VertexAttribute(0, 3, GL_FLOAT, false));
        layout.addAttribute(new VertexAttribute(1, 4, GL_UNSIGNED_BYTE, true));

        ByteBuffer data = meshBuilder
                .pos(-0.2f, 0.0f, -0.2f).color(1.0f, 0.0f, 0.0f, 1.0f).endVertex()
                .pos( 0.2f, 0.0f, -0.2f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex()
                .pos( 0.0f, 0.2f,  0.0f).color(0.0f, 0.0f, 1.0f, 1.0f).endVertex()
                .buildBuffer(layout);
        mesh.uploadAndConfigure(data, layout, GL_STATIC_DRAW);

        Material material = new Material(ShaderManager.getPositionColorShader());

        TestObj testObj = new TestObj(mesh, material);
        testObj.getTransform().setPosition(10, 100, 10);
        this.addRenderable(testObj);

    }
}
