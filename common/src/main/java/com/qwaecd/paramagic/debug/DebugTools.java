package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.client.renderbase.factory.SphereFactory;
import com.qwaecd.paramagic.core.render.IRenderable;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttribute;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;

public class DebugTools {
    public static void test() {
        SphereFactory sphereFactory = new SphereFactory();
        AbstractMaterial material = new DeBugMaterial(ShaderManager.getBaseBallInShader());
        IRenderable obj = sphereFactory.withMaterial(material).createInstance();
        ModRenderSystem.getInstance().addRenderable(obj);

//        addRenderable(MagicCircleFactory.create(new ResourceLocation(Constants.MOD_ID, "textures/magic/circle_01.png")));
    }

    // 使用顶点数组构建一个立方体，包含六个面，每个面由两个三角形组成
    private TestObj addTestObj() {
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

        AbstractMaterial material = new Material(ShaderManager.getPositionColorShader());

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

        AbstractMaterial material = new Material(ShaderManager.getMagicRingShader());

        TestObj obj = new TestObj(mesh, material);
        obj.getTransform().setPosition(0, 0, 0);
        ModRenderSystem.getInstance().addRenderable(obj);
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

        AbstractMaterial material = new Material(ShaderManager.getMagicRingShader());
        TestObj obj = new TestObj(mesh, material);
        obj.getTransform().setPosition(0, 0, 0);

        ModRenderSystem.getInstance().addRenderable(obj);
    }
}
