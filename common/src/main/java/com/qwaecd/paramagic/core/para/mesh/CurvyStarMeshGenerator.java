package com.qwaecd.paramagic.core.para.mesh;

import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import org.joml.*;

import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.qwaecd.paramagic.core.para.mesh.ParaMeshProvider.DEFAULT_LAYOUT_POS_COLOR;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class CurvyStarMeshGenerator {
    private final Map<String, Mesh> curvyStarCache;

    // 生成圆弧时的迭代次数
    private static final int stepCount = 32;
    private static final Matrix2f rotate180 = new Matrix2f().rotate((float) Math.toRadians(180));

    public CurvyStarMeshGenerator() {
        this.curvyStarCache = new HashMap<>();
    }

    /**
     * Get or create a curvy polygon star mesh.<br>
     * 生成一个边是圆弧的正n边星网格。如果缓存中已有相同参数的网格，则直接返回缓存的网格。
     * @param radius 多边形外接圆半径。
     * @param sides 多边形边长，最小为3.
     * @param curvature 曲率，取值不为零即可。意义为取图元中心为原点，初始方向正方向的坐标系，圆弧在该坐标系上的坐标 alpha 与外接圆半径的比值。<br>
     *           {@code curvature = radius / alpha}。
     */
    public Mesh get(Vector4f color, float radius, int sides, float curvature, float startAngle, float lineWidth) {
        final float newK = curvature == 0.0f ? 0.1f : curvature;
        String key = String.format(
                "curvy_star:(%.2f-%.2f-%.2f-%.2f)-%d-%.4f-%.4f-%.4f-%.4f",
                color.x, color.y, color.z, color.w,
                sides, radius, newK, startAngle, lineWidth);
        return this.curvyStarCache.computeIfAbsent(key, k -> createCurvyStarMesh(color, radius, sides, radius / newK, startAngle, lineWidth));
    }

    private Mesh createCurvyStarMesh(Vector4f color, float radius, int sides, float alpha, float startAngle, float lineWidth) {
        MeshBuilder builder = new MeshBuilder();

        // 多边形中心单个扇形的圆心角 theta
        final double Theta = 2 * Math.PI / sides;
        // 弧圆心 OP 向量
        @SuppressWarnings("UnnecessaryLocalVariable")
        final float OP = alpha;
        // 弧圆半径 r
        final double r = r(OP, radius, Theta);
        // 弧圆心角的一半 phi
        final double cosPhi = (r*r + OP*OP - radius*radius) / (2 * r * Math.abs(OP));
        final double phi = alpha > 0.0f ?  Math.acos(cosPhi) : Math.acos(cosPhi) + Math.PI;

        final double angleStep = phi / stepCount;
        Matrix2f rotationMatrix = new Matrix2f();
        // 对每一个顶点都进行一次画圆弧操作
        for (int i = 0; i < sides; i++) {
            int vertexCount = 0;
//            int lastIdx = i - 1 == -1 ? sides - 1 : i - 1;
//            int nextIdx = i + 1 == sides ? 0 : i + 1;

            double beta = 0.0d;
            rotationMatrix.identity();
            rotationMatrix.rotate((float) (startAngle + i * Theta));
            for (int j = 0; j <= stepCount; j++) {
                float outerRadius = (float) (r + lineWidth / 2.0f);
                float innerRadius = (float) Math.max(0.0f, r - lineWidth / 2.0f);
                // 计算单个小短线的坐标
                // A B -> 下一个循环会出现
                // C D
                double cosBeta = Math.cos(beta);
                double sinBeta = Math.sin(beta);

                Vector2f C = new Vector2f(
                        (float)  (outerRadius * cosBeta),
                        (float)  (outerRadius * sinBeta)
                ).mul(rotate180).add(OP, 0.0f).mul(rotationMatrix);
                Vector2f D = new Vector2f(
                        (float)  (innerRadius * cosBeta),
                        (float)  (innerRadius * sinBeta)
                ).mul(rotate180).add(OP, 0.0f).mul(rotationMatrix);
                // 同时生成关于 x 轴对称的一对
                Vector2f C_1 = new Vector2f(
                        (float)  (outerRadius * cosBeta),
                        (float) -(outerRadius * sinBeta)
                ).mul(rotate180).add(OP, 0.0f).mul(rotationMatrix);
                Vector2f D_1 = new Vector2f(
                        (float)  (innerRadius * cosBeta),
                        (float) -(innerRadius * sinBeta)
                ).mul(rotate180).add(OP, 0.0f).mul(rotationMatrix);


                // 是的, y 分量恒定为0
                builder.pos(C.x, 0, C.y).color(color.x, color.y, color.z, color.w).endVertex();
                builder.pos(D.x, 0, D.y).color(color.x, color.y, color.z, color.w).endVertex();
                // 同时生成关于 x 轴对称的一对
                builder.pos(C_1.x, 0, C_1.y).color(color.x, color.y, color.z, color.w).endVertex();
                builder.pos(D_1.x, 0, D_1.y).color(color.x, color.y, color.z, color.w).endVertex();

                beta += angleStep;
                vertexCount += 4;
            }

            // 生成索引
            final int idxOffset = 2;
            for (int j = 0; j < stepCount; j++) {
                // 由于必须关闭面剔除，所以环绕顺序无所谓
                // A B
                // C D
                // CDA, ADB
                int idxC = (j * 4) + i * vertexCount;
                int idxD = (j * 4 + 1) + i * vertexCount;
                int idxA = (j * 4 + 4) + i * vertexCount;
                int idxB = (j * 4 + 5) + i * vertexCount;
                builder.addTriangle(idxC, idxD, idxA);
                builder.addTriangle(idxA, idxD, idxB);
                // 对称的一对
                builder.addTriangle(idxC + idxOffset, idxD + idxOffset, idxA + idxOffset);
                builder.addTriangle(idxA + idxOffset, idxD + idxOffset, idxB + idxOffset);
            }
        }
        ByteBuffer vertexData = builder.buildBuffer(DEFAULT_LAYOUT_POS_COLOR);
        ShortBuffer indexData = builder.buildIndexBufferU16();

        Mesh mesh = new Mesh(Mesh.DrawMode.TRIANGLES);
        mesh.uploadAndConfigure(vertexData, DEFAULT_LAYOUT_POS_COLOR, GL_STATIC_DRAW, indexData, GL_STATIC_DRAW);
        return mesh;
    }

    private double r(float OP, float radius, double theta) {
        // 计算圆弧半径 r
        final Vector2d OM_0 = new Vector2d(OP, 0);
        final Vector2d OM_1 = new Vector2d(
                radius * Math.cos(theta * 1),
                radius * Math.sin(theta * 1)
        );
        return Math.sqrt(
                (OM_0.x - OM_1.x)*(OM_0.x - OM_1.x) + (OM_0.y - OM_1.y) * (OM_0.y - OM_1.y)
        );
    }
}
