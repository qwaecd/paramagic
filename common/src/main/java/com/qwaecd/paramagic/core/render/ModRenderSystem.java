package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.Constants;
import com.qwaecd.paramagic.client.renderbase.BaseObjectManager;
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
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
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

            material.apply();

            Shader shader = material.getShader();
            shader.setUniformMatrix4f("u_projection", projectionMatrix);
            shader.setUniformMatrix4f("u_view", view);
            shader.setUniformMatrix4f("u_model", relativeModelMatrix);
            shader.setUniformValue1f("u_time", timeSeconds);

            renderable.getTransform()
                    .translate(
                            (float) Math.sin(timeSeconds),
                            (float) Math.cos(timeSeconds),
                            0.0f
                    )
                    .setScale((float) Math.sin(timeSeconds)*20.0f + 25.0f, (float) Math.sin(timeSeconds)*20.0f + 25.0f, (float) Math.sin(timeSeconds)*20.0f + 25.0f);
//            glDisable(GL_CULL_FACE);
            renderable.getMesh().draw();
//            glEnable(GL_CULL_FACE);
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

    public void test() {
//        addTestObj();
//        addMagicRingIndexedTest();
        addRenderable(BaseObjectManager.getBASE_BALL());
    }

    private void addTestObj() {

        this.addRenderable(add());
    }
    // 使用顶点数组构建一个立方体，包含六个面，每个面由两个三角形组成
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
    // 使用顶点数组 vbo 构建方形
    public void addMagicRingTest() {
        MeshBuilder builder = new MeshBuilder();
        Mesh mesh = new Mesh(GL_TRIANGLES);

        // 只用位置属性（location = 0）
        VertexLayout layout = new VertexLayout();
        layout.addAttribute(new VertexAttribute(0, 3, GL_FLOAT, false));

        // 简单平面（XZ），两三角构成一个 4x4 的方形
        ByteBuffer data = builder
                .pos(-2.0f, 0.0f, -2.0f).endVertex()
                .pos( 2.0f, 0.0f, -2.0f).endVertex()
                .pos( 2.0f, 0.0f,  2.0f).endVertex()
                .pos( 2.0f, 0.0f,  2.0f).endVertex()
                .pos(-2.0f, 0.0f,  2.0f).endVertex()
                .pos(-2.0f, 0.0f, -2.0f).endVertex()
                .buildBuffer(layout);

        mesh.uploadAndConfigure(data, layout, GL_STATIC_DRAW);

        Material material = new Material(ShaderManager.getMagicRingShader());

        TestObj obj = new TestObj(mesh, material);
        obj.getTransform().setPosition(0, 0, 0);
        this.addRenderable(obj);
    }
    // 使用顶点索引数组 ebo 构建方形
    public void addMagicRingIndexedTest() {
        MeshBuilder builder = new MeshBuilder();
        Mesh mesh = new Mesh(GL_TRIANGLES);

        // 只用位置属性（location = 0）
        VertexLayout layout = new VertexLayout();
        layout.addAttribute(new VertexAttribute(0, 3, GL_FLOAT, false));

        // 顶点：XZ 平面上的 2x2 方形（与之前 arrays 版本一致）
        ByteBuffer vertices = builder
                .pos(-2.0f, 0.0f, -2.0f).endVertex() // 0
                .pos( 2.0f, 0.0f, -2.0f).endVertex() // 1
                .pos( 2.0f, 0.0f,  2.0f).endVertex() // 2
                .pos(-2.0f, 0.0f,  2.0f).endVertex() // 3
                .buildBuffer(layout);

        // 索引：两个三角形组成一个四边形
        ShortBuffer indices = BufferUtils.createShortBuffer(6)
                .put(new short[]{0, 1, 2, 2, 3, 0});
        indices.flip();

        mesh.uploadAndConfigure(vertices, layout, GL_STATIC_DRAW, indices, GL_STATIC_DRAW);

        Material material = new Material(ShaderManager.getMagicRingShader());
        TestObj obj = new TestObj(mesh, material);
        obj.getTransform().setPosition(0, 0, 0);

        this.addRenderable(obj);
    }
}
