package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.client.material.EmissiveMagicMaterial;
import com.qwaecd.paramagic.client.obj.sun.Sun;
import com.qwaecd.paramagic.client.renderbase.factory.SphereFactory;
import com.qwaecd.paramagic.client.renderbase.factory.UnitQuadFactory;
import com.qwaecd.paramagic.core.render.IRenderable;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttribute;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;
import com.qwaecd.paramagic.data.para.ParaData;
import com.qwaecd.paramagic.data.para.PolygonParaData;
import com.qwaecd.paramagic.data.para.RingParaData;
import com.qwaecd.paramagic.data.para.VoidParaData;
import com.qwaecd.paramagic.data.para.converter.ConversionException;
import com.qwaecd.paramagic.data.para.converter.ParaConverters;
import com.qwaecd.paramagic.feature.MagicCircle;
import com.qwaecd.paramagic.feature.MagicCircleManager;
import lombok.experimental.UtilityClass;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;

@UtilityClass
public class DebugTools {
    public static void test() {
//        tooManyMagicCircles();
        IRenderable ball;
        SphereFactory sphereFactory = new SphereFactory();
        Material material = new Material(ShaderManager.getInstance().getBaseBallInShader());
        ball = sphereFactory.withMaterial(material).createInstance();
        ball.getTransform().getModelMatrix().translate(0, 100, 0).scale(4.0f, 4.0f, 4.0f);
//        ModRenderSystem.getInstance().addRenderable(ball);

        IRenderable sun = new Sun(ShaderManager.getInstance().getShader("sun"));
        sun.getTransform().setPosition(0, 80, 10).setScale(5.0f, 5.0f, 5.0f);
        ModRenderSystem.getInstance().addRenderable(sun);
        paraTest();
    }

    private static void paraTest() {
        VoidParaData rootPara = new VoidParaData("void_1");

        rootPara.addChild(new RingParaData(
                "ring_1",
                4.0f, 4.2f,
                64
        ));
        rootPara.addChild(new RingParaData(
                "ring_3",
                3.8f, 3.9f,
                64
        ));


        RingParaData ring2 = new RingParaData(
                "ring_2",
                2.0f, 2.1f,
                64
        );
        ring2.position.set(0, 0.09f, 0);
        ring2.color.set(0.7f, 0.7f, 0.2f);
        rootPara.addChild(ring2);

        {
            VoidParaData group = new VoidParaData("group_1");
            group.position.set(0, 0.2f, 0);

            PolygonParaData polygon1 = new PolygonParaData(
                    "polygon_1",
                    3.0f,
                    3,
                    0.0f,
                    0.1f
            );
            polygon1.position.set(0, 0.1f, 0);
            polygon1.color.set(0.2f, 0.7f, 0.7f);
            group.addChild(polygon1);

            PolygonParaData polygon2 = new PolygonParaData(
                    "polygon_2",
                    3.0f,
                    3,
                    (float) Math.toRadians(60),
                    0.1f
            );
            polygon2.position.set(0, 0.2f, 0);
            polygon2.color.set(0.6f, 0.1f, 0.3f);
            group.addChild(polygon2);

            rootPara.addChild(group);
        }

        ParaData paraData = new ParaData(rootPara);

        try {
            MagicCircle magicCircle = ParaConverters.convert(paraData);
            magicCircle.getTransform()
                    .setPosition(0 , 100.01f , 0)
                    .setRotationDegrees(90.0f, 0, 0)
                    .setScale(1.0f, 1.0f, 1.0f);
            MagicCircleManager.getInstance().addCircle(magicCircle);
        } catch (ConversionException e) {
            Paramagic.LOG.error("Para conversion error: ", e);
        }
    }

    private static void tooManyMagicCircles() {
        IRenderable obj;
        UnitQuadFactory factory = new UnitQuadFactory();

        // 主法阵 - 最大的底层法阵
        obj = factory
                .withMaterial(new EmissiveMagicMaterial(ShaderManager.getInstance().getShaderThrowIfNotFound("emissive_magic")))
                .createInstance();
        obj.getTransform().getModelMatrix().translate(20, 200, 0).scale(20.0f, 20.0f, 20.0f).rotateY((float)Math.toRadians(45));
        ModRenderSystem.getInstance().addRenderable(obj);

        // 中层法阵群 - 围绕主法阵的多个中等大小法阵
        for (int i = 0; i < 6; i++) {
            IRenderable midCircle = factory.createInstance();
            double angle = i * Math.PI / 3; // 每60度一个
            float x = 20 + (float)(Math.cos(angle) * 25);
            float z = 0 + (float)(Math.sin(angle) * 25);
            float scale = 12.0f - i * 0.8f;
            float rotationY = (float)Math.toRadians(i * 60 + 30);
            float rotationX = (float)Math.toRadians(15);
            midCircle.getTransform().getModelMatrix().translate(x, 202, z).scale(scale, scale, scale)
                    .rotateX(rotationX).rotateY(rotationY);
            ModRenderSystem.getInstance().addRenderable(midCircle);
        }

        // 内层法阵群 - 小法阵群，更密集
        for (int i = 0; i < 8; i++) {
            IRenderable innerCircle = factory.createInstance();
            double angle = i * Math.PI / 4; // 每45度一个
            float x = 20 + (float)(Math.cos(angle) * 12);
            float z = 0 + (float)(Math.sin(angle) * 12);
            float scale = 4.0f + i * 0.5f;
            float rotationY = (float)Math.toRadians(i * 45);
            float rotationZ = (float)Math.toRadians(10);
            innerCircle.getTransform().getModelMatrix().translate(x, 204, z).scale(scale, scale, scale)
                    .rotateY(rotationY).rotateZ(rotationZ);
            ModRenderSystem.getInstance().addRenderable(innerCircle);
        }

        // 垂直层叠法阵 - 在Y轴上叠加的不同大小法阵
        for (int i = 0; i < 7; i++) {
            IRenderable verticalCircle = factory.createInstance();
            float y = 200 + i * 6;
            float scale = 18.0f - i * 2.5f;
            float rotationY = (float)Math.toRadians(i * 30);
            float rotationX = (float)Math.toRadians(i * 5);
            verticalCircle.getTransform().getModelMatrix().translate(20, y, 0).scale(scale, scale, scale)
                    .rotateX(rotationX).rotateY(rotationY);
            ModRenderSystem.getInstance().addRenderable(verticalCircle);
        }

        // 外环大法阵 - 更远距离的大型法阵
        for (int i = 0; i < 4; i++) {
            IRenderable outerCircle = factory.createInstance();
            double angle = i * Math.PI / 2; // 每90度一个
            float x = 20 + (float)(Math.cos(angle) * 40);
            float z = 0 + (float)(Math.sin(angle) * 40);
            float scale = 15.0f + i * 2;
            float rotationY = (float)Math.toRadians(i * 90 + 45);
            float rotationX = (float)Math.toRadians(20);
            outerCircle.getTransform().getModelMatrix().translate(x, 198, z).scale(scale, scale, scale)
                    .rotateX(rotationX).rotateY(rotationY);
            ModRenderSystem.getInstance().addRenderable(outerCircle);
        }

        // 高空法阵层 - 更高的法阵
        for (int i = 0; i < 5; i++) {
            IRenderable highCircle = factory.createInstance();
            double angle = i * Math.PI * 2 / 5; // 每72度一个
            float x = 20 + (float)(Math.cos(angle) * 30);
            float z = 0 + (float)(Math.sin(angle) * 30);
            float y = 220 + i * 8;
            float scale = 10.0f - i * 1.5f;
            float rotationY = (float)Math.toRadians(i * 72);
            float rotationX = (float)Math.toRadians(-25);
            highCircle.getTransform().getModelMatrix().translate(x, y, z).scale(scale, scale, scale)
                    .rotateX(rotationX).rotateY(rotationY);
            ModRenderSystem.getInstance().addRenderable(highCircle);
        }

        // 随机散布的小法阵 - 增加层次感
        for (int i = 0; i < 15; i++) {
            IRenderable randomCircle = factory.createInstance();
            float x = 20 + (float)(Math.random() * 60 - 30); // -30到30的随机偏移
            float z = 0 + (float)(Math.random() * 60 - 30);
            float y = 195 + (float)(Math.random() * 40); // Y轴195-235随机
            float scale = 3.0f + (float)(Math.random() * 6); // 3-9的随机大小
            float rotationX = (float)Math.toRadians(Math.random() * 360);
            float rotationY = (float)Math.toRadians(Math.random() * 360);
            float rotationZ = (float)Math.toRadians(Math.random() * 360);
            randomCircle.getTransform().getModelMatrix().translate(x, y, z).scale(scale, scale, scale)
                    .rotateX(rotationX).rotateY(rotationY).rotateZ(rotationZ);
            ModRenderSystem.getInstance().addRenderable(randomCircle);
        }
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

        AbstractMaterial material = new Material(ShaderManager.getInstance().getPositionColorShader());

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

        AbstractMaterial material = new Material(ShaderManager.getInstance().getMagicRingShader());

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

        AbstractMaterial material = new Material(ShaderManager.getInstance().getMagicRingShader());
        TestObj obj = new TestObj(mesh, material);
        obj.getTransform().setPosition(0, 0, 0);

        ModRenderSystem.getInstance().addRenderable(obj);
    }
}
