package com.qwaecd.paramagic.client.renderbase.factory;

import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttributeEnum;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;

public class FullScreenQuadFactory {

    public static Mesh createFullscreenQuad() {
        MeshBuilder builder = new MeshBuilder();
        // 顶点0: 左上
        builder.pos(-1.0f, 1.0f, 0.0f).uv(0.0f, 1.0f).endVertex();
        // 顶点1: 左下
        builder.pos(-1.0f, -1.0f, 0.0f).uv(0.0f, 0.0f).endVertex();
        // 顶点2: 右下
        builder.pos(1.0f, -1.0f, 0.0f).uv(1.0f, 0.0f).endVertex();
        // 顶点3: 右上
        builder.pos(1.0f, 1.0f, 0.0f).uv(1.0f, 1.0f).endVertex();
        builder.addTriangle(0, 1, 2);
        builder.addTriangle(0, 2, 3);
        Mesh quadMesh = new Mesh(GL_TRIANGLES);

        VertexLayout layout = getPostProcessingLayout();

        ByteBuffer vertexBuffer = builder.buildBuffer(layout);
        ShortBuffer indexBuffer = builder.buildIndexBufferU16();
        quadMesh.uploadAndConfigure(
                vertexBuffer,
                layout,
                GL_STATIC_DRAW,
                indexBuffer,
                GL_STATIC_DRAW
        );
        return quadMesh;
    }

    public static VertexLayout getPostProcessingLayout() {
        VertexLayout vertexLayout = new VertexLayout();
        vertexLayout.addAttribute(VertexAttributeEnum.POSITION.get());
        vertexLayout.addAttribute(VertexAttributeEnum.UV.get());
        return vertexLayout;
    }
}
