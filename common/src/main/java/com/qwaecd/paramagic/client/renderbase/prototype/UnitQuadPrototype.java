package com.qwaecd.paramagic.client.renderbase.prototype;

import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttribute;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;
import lombok.Getter;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class UnitQuadPrototype implements IShapePrototype {
    private final Mesh mesh;
    @Getter
    private static final UnitQuadPrototype INSTANCE = new UnitQuadPrototype();

    public UnitQuadPrototype() {
        this.mesh = new Mesh(GL_TRIANGLES);
        buildUniQuadMesh();
    }

    public static void init() {
    }

    private void buildUniQuadMesh() {
        VertexLayout layout = new VertexLayout();
        layout
            .addNextAttribute(new VertexAttribute(0, 3, GL_FLOAT, false));
        MeshBuilder meshBuilder = new MeshBuilder();
        meshBuilder.pos(-1.0f,  0.0f,-1.0f).endVertex()
                   .pos( 1.0f,  0.0f,-1.0f).endVertex()
                   .pos( 1.0f,  0.0f, 1.0f).endVertex()
                   .pos( 1.0f,  0.0f, 1.0f).endVertex()
                   .pos(-1.0f,  0.0f, 1.0f).endVertex()
                   .pos(-1.0f,  0.0f,-1.0f).endVertex();

        ByteBuffer vertexData = meshBuilder.buildBuffer(layout);

        this.mesh.uploadAndConfigure(vertexData, layout, GL_STATIC_DRAW);
    }

    @Override
    public Mesh getMesh() {
        return this.mesh;
    }
}
