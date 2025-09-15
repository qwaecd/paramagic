package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.assembler.AssemblyException;
import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.client.obj.sun.Sun;
import com.qwaecd.paramagic.client.renderbase.factory.SphereFactory;
import com.qwaecd.paramagic.core.render.IRenderable;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttribute;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;
import com.qwaecd.paramagic.data.animation.AnimationBindingData;
import com.qwaecd.paramagic.data.animation.AnimatorData;
import com.qwaecd.paramagic.data.animation.BindingData;
import com.qwaecd.paramagic.data.animation.KeyframeProperties;
import com.qwaecd.paramagic.data.animation.track.KeyframeData;
import com.qwaecd.paramagic.data.animation.track.KeyframeTrackData;
import com.qwaecd.paramagic.data.animation.track.TrackData;
import com.qwaecd.paramagic.data.para.*;
import com.qwaecd.paramagic.feature.MagicCircle;
import com.qwaecd.paramagic.feature.MagicCircleManager;
import lombok.experimental.UtilityClass;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;

@UtilityClass
public class DebugTools {
    public static void test() {
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
        ParaData paraData = genParaData();
        try {

            MagicCircle circle = testInjectAnimation(paraData);

            modifyMagicCircle(circle);

            MagicCircleManager.getInstance().addCircle(circle);
        } catch (ConversionException e) {
            Paramagic.LOG.error("Para conversion error: ", e);
        } catch (AssemblyException e) {
            Paramagic.LOG.error("Assembly error: ", e);
        }
    }

    private MagicCircle testInjectAnimation(ParaData paraData) throws AssemblyException, ConversionException {
        ParaComposer composer = ParaComposer.getINSTANCE();
        MagicCircle circle = composer.assemble(paraData, genAnimationBindingData(), null);

        return circle;
    }

    private AnimationBindingData genAnimationBindingData() {
        List<BindingData> bindingDataList = new ArrayList<>();

        AnimatorData animatingCenter;
        {
            TrackData rotationTrack = new KeyframeTrackData<>(
                    KeyframeProperties.ROTATION.type(),
                    List.of(
                            new KeyframeData<>(0f, new Quaternionf().rotateY((float)Math.toRadians(0))),
                            new KeyframeData<>(1.0f, new Quaternionf().rotateY((float)Math.toRadians(180))),
                            new KeyframeData<>(2.0f, new Quaternionf().rotateY((float)Math.toRadians(359)))
                    ),
                    true
            );

            TrackData scaleTrack = new KeyframeTrackData<>(
                    KeyframeProperties.SCALE.type(),
                    List.of(
                            new KeyframeData<>(0f, new Vector3f(0.0f, 0.0f, 0.0f)),
                            new KeyframeData<>(3.0f, new Vector3f(0.5f)),
                            new KeyframeData<>(6.0f, new Vector3f(1)),
                            new KeyframeData<>(24.0f, new Vector3f(2))
                    ),
                    true
            );

            animatingCenter = new AnimatorData(List.of(rotationTrack, scaleTrack));
        }

        BindingData data1 = new BindingData(
                "root.3",
                null,
                animatingCenter
        );
        bindingDataList.add(data1);
        // --------------------------------------------
        AnimatorData animatingColor;
        {
            TrackData colorTrack = new KeyframeTrackData<>(
                    KeyframeProperties.COLOR.type(),
                    List.of(
                            new KeyframeData<>(1.0f, new Vector4f(1.0f, 0.0f, 0.0f, 0.5f)), // Red
                            new KeyframeData<>(2.0f, new Vector4f(1.0f, 1.0f, 0.0f, 0.75f)),// Yellow
                            new KeyframeData<>(3.0f, new Vector4f(0.0f, 1.0f, 1.0f, 1.0f)), // Cyan
                            new KeyframeData<>(4.0f, new Vector4f(1.0f, 0.0f, 0.0f, 0.5f))  // Red
                    ),
                    false
            );
            animatingColor = new AnimatorData(List.of(colorTrack));
        }
        BindingData data2 = new BindingData(
                "root.0",
                null,
                animatingColor
        );
        bindingDataList.add(data2);

        return new AnimationBindingData(bindingDataList);
    }

    private void modifyMagicCircle(MagicCircle circle) throws ConversionException {
        circle.getTransform()
                .setPosition(0 , 110.01f , 0)
                .setRotationDegrees(0.0f, 0, 0)
                .setScale(1.0f, 1.0f, 1.0f);
    }

    private ParaData genParaData() {
        VoidParaData rootPara = new VoidParaData();

        rootPara.addChild(new RingParaData(
                4.0f, 4.2f,
                64
        ));
        rootPara.addChild(new RingParaData(
                3.8f, 3.9f,
                64
        ));


        RingParaData ring2 = new RingParaData(
                2.0f, 2.1f,
                64
        );
        ring2.position.set(0, 0.09f, 0);
        ring2.color.set(0.7f, 0.7f, 0.2f);
        rootPara.addChild(ring2);

        {
            VoidParaData group = new VoidParaData();

            group.position.set(0, 0.2f, 0);

            PolygonParaData polygon1 = new PolygonParaData(
                    3.0f,
                    3,
                    0.0f,
                    0.1f
            );
            polygon1.position.set(0, 0.1f, 0);
            polygon1.color.set(0.2f, 0.7f, 0.7f);
            group.addChild(polygon1);

            PolygonParaData polygon2 = new PolygonParaData(
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
        return paraData;
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
}
