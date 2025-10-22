package com.qwaecd.paramagic.core.para.mesh;

import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import org.joml.Vector4f;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.qwaecd.paramagic.core.para.mesh.ParaMeshProvider.DEFAULT_LAYOUT_POS_COLOR;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class PolygonMeshGenerator {
    private final Map<String, Mesh> polygonCache;

    public PolygonMeshGenerator() {
        this.polygonCache = new HashMap<>();
    }

    public Mesh get(Vector4f color, float radius, int sides, float startAngle, float lineWidth) {
        String key = String.format(
                "polygon:(%.2f-%.2f-%.2f-%.2f)-%d-%.4f-%.4f-%.4f",
                color.x, color.y, color.z, color.w,
                sides, radius, startAngle, lineWidth);
        return this.polygonCache.computeIfAbsent(key, k -> createPolygonMesh(color, radius, sides, startAngle, lineWidth));
    }

    private Mesh createPolygonMesh(Vector4f color, float radius, int sides, float startAngle, float lineWidth) {
        MeshBuilder builder = new MeshBuilder();

        float outerRadius = radius + lineWidth / 2.0f;
        float innerRadius = Math.max(0.0f, radius - lineWidth / 2.0f);

        for (int i = 0; i <= sides; i++) {
            float angle = startAngle + (float) (i * 2.0 * Math.PI / sides);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            // 外圈顶点
            float ox = outerRadius * cos;
            float oz = outerRadius * sin;
            builder.pos(ox, 0, oz).color(color.x, color.y, color.z, color.w).endVertex();
            // 内圈顶点
            float ix = innerRadius * cos;
            float iz = innerRadius * sin;
            builder.pos(ix, 0, iz).color(color.x, color.y, color.z, color.w).endVertex();
        }

        for (int i = 0; i < sides; i++) {
            // 顶点索引布局:
            // 2*i      : 当前顶点的外圈顶点
            // 2*i + 1  : 当前顶点的内圈顶点
            // 2*i + 2  : 下一顶点的外圈顶点
            // 2*i + 3  : 下一顶点的内圈顶点
            int baseOuter = i * 2;
            int baseInner = i * 2 + 1;
            int nextOuter = i * 2 + 2;
            int nextInner = i * 2 + 3;
            // 第一个三角形: (当前外, 下一外, 下一内)
            builder.addTriangle(baseOuter, nextOuter, nextInner);
            // 第二个三角形: (当前外, 下一内, 当前内)
            builder.addTriangle(baseOuter, nextInner, baseInner);
        }
        ByteBuffer vertexData = builder.buildBuffer(DEFAULT_LAYOUT_POS_COLOR);
        ShortBuffer indexData = builder.buildIndexBufferU16();

        Mesh mesh = new Mesh(Mesh.DrawMode.TRIANGLES);
        mesh.uploadAndConfigure(vertexData, DEFAULT_LAYOUT_POS_COLOR, GL_STATIC_DRAW, indexData, GL_STATIC_DRAW);
        return mesh;
    }
}
