package com.qwaecd.paramagic.core.para.mesh;

import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttributeEnum;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;
import org.joml.Vector4f;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.qwaecd.paramagic.core.para.mesh.ParaMeshProvider.DEFAULT_LAYOUT_POS_COLOR;
import static org.lwjgl.opengl.GL33.GL_STATIC_DRAW;


public class RingMeshGenerator {
    private final Map<String, Mesh> ringCache;

    public RingMeshGenerator() {
        this.ringCache = new HashMap<>();
    }

    /**
     * Gets a ring mesh. If a mesh with the same parameters already exists in the cache,
     * it's returned; otherwise, a new one is created, cached, and returned.
     *
     * @param innerRadius The radius of the inner circle.
     * @param outerRadius The radius of the outer circle.
     * @param segments    The number of segments to approximate the circle. More segments mean a smoother ring.
     * @return A non-null, configured Mesh instance for the ring.
     */
    public Mesh get(Vector4f color, float innerRadius, float outerRadius, int segments) {
        String key = String.format(
                "ring:(%.2f-%.2f-%.2f-%.2f)-%.4f-%.4f-%d",
                color.x, color.y, color.z, color.w,
                innerRadius, outerRadius, segments);
        return this.ringCache.computeIfAbsent(key, k -> createRingMesh(color, innerRadius, outerRadius, segments));
    }

    private Mesh createRingMesh(Vector4f color, float innerRadius, float outerRadius, int segments) {
        MeshBuilder builder = new MeshBuilder();
        for (int i = 0; i <= segments; i++) {
            // 计算当前段的角度
            float angle = (float) (i * 2.0 * Math.PI / segments);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            // 内圈顶点
            float ix = innerRadius * cos;
            float iz = innerRadius * sin;
            // 外圈顶点
            float ox = outerRadius * cos;
            float oz = outerRadius * sin;
            // 添加内圈顶点
            builder.pos(ix, 0, iz).color(color.x, color.y, color.z, color.w).endVertex();
            // 添加外圈顶点
            builder.pos(ox, 0, oz).color(color.x, color.y, color.z, color.w).endVertex();
        }
        // 2. 生成索引
        // 每个段由两个三角形组成一个四边形
        for (int i = 0; i < segments; i++) {
            // 每个段有4个顶点，但由于顶点是成对生成的，所以索引基数是 i * 2
            int base = i * 2;

            // 顶点索引:
            // base      : 当前段的内圈顶点
            // base + 1  : 当前段的外圈顶点
            // base + 2  : 下一段的内圈顶点
            // base + 3  : 下一段的外圈顶点

            // (base, base + 1, base + 3) 和 (base, base + 3, base + 2)
            // 三角形1: (base, base + 1, base + 3) -> (内, 外, 下一外)
            // 三角形2: (base, base + 3, base + 2) -> (内, 下一外, 下一内)
            builder.addTriangle(base, base + 1, base + 3);
            builder.addTriangle(base, base + 3, base + 2);
        }

        ByteBuffer vertexData = builder.buildBuffer(DEFAULT_LAYOUT_POS_COLOR);
        ShortBuffer indexData = builder.buildIndexBufferU16();

        Mesh mesh = new Mesh(Mesh.DrawMode.TRIANGLES);
        mesh.uploadAndConfigure(vertexData, DEFAULT_LAYOUT_POS_COLOR, GL_STATIC_DRAW, indexData, GL_STATIC_DRAW);
        return mesh;
    }
}
