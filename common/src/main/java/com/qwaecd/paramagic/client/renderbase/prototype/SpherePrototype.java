package com.qwaecd.paramagic.client.renderbase.prototype;

import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttribute;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class SpherePrototype implements IShapePrototype {
    public static final int RADIUS = 1;
    @Getter
    private int stacks;   // 纬线
    @Getter
    private int slices;   // 经线
    private final Mesh mesh;
    private final List<Integer> indices = new ArrayList<>();
    @Getter
    private static final SpherePrototype INSTANCE = new SpherePrototype();
    public SpherePrototype() {
        this.mesh = new Mesh(GL_TRIANGLES);
        this.stacks = 32;
        this.slices = 64;
        buildSphereMesh();
    }

    public static void init() {
    }

    private void buildSphereMesh() {
        VertexLayout layout = new VertexLayout();
        layout
        .addNextAttribute(new VertexAttribute(0, 3, GL_FLOAT, false))
        .addNextAttribute(new VertexAttribute(1, 4, GL_UNSIGNED_BYTE, true))
        .addNextAttribute(new VertexAttribute(3, 3, GL_BYTE, true));


        MeshBuilder builder = new MeshBuilder();
        // 创建顶点
        for (int i = 0; i < this.stacks + 1; i++) {
            float phi = (float) Math.PI * i / this.stacks;  // 0->北极，1->南极
            for (int j = 0; j < this.slices + 1; j++) {
                float theta = (float) (2 * Math.PI / this.slices * j);
                float x = (float) (RADIUS * Math.sin(phi) * Math.cos(theta));
                float y = (float) (RADIUS * Math.cos(phi));
                float z = (float) (RADIUS * Math.sin(phi) * Math.sin(theta));
                builder.pos(x, y, z).color(0.0f, 1.0f, 1.0f, 1.0f).normal(x, y, z).endVertex();
            }
        }
        ByteBuffer vertexData = builder.buildBuffer(layout);
        // 创建索引
        for (int i = 0; i < this.stacks; i++) {
            for (int j = 0; j < this.slices; j++) {
                int i0 = j + i * (this.slices + 1); // 左上
                int i1 = j + (i + 1) * (this.slices + 1);   // 左下
                int i2 = j + 1 + (i + 1) * (this.slices + 1);   // 右下
                int i3 = j + 1 + i * (this.slices + 1); // 右上
                this.indices.add(i0);
                builder.addTriangle(
                        i0,
                        i2,
                        i1
                );
                builder.addTriangle(
                        i0,
                        i3,
                        i2
                );
            }
        }
        ShortBuffer indexBuffer = builder.buildIndexBufferU16();

        this.mesh.uploadAndConfigure(vertexData, layout, GL_STATIC_DRAW, indexBuffer, GL_STATIC_DRAW);
    }

    @Override
    public Mesh getMesh() {
        return this.mesh;
    }
}
