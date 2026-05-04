package com.qwaecd.paramagic.client.renderbase;

import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttribute;
import com.qwaecd.paramagic.core.render.vertex.VertexAttributeEnum;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public final class SharedMeshes {
    private static final int SPHERE_RADIUS = 1;
    private static final int SPHERE_STACKS = 32;
    private static final int SPHERE_SLICES = 64;
    private static final int UNIT_CYLINDER_SIDE_QUADS = 32;
    private static final float UNIT_CYLINDER_RADIUS = 1.0f;
    private static final float UNIT_CYLINDER_HEIGHT = 1.0f;

    private static Mesh sphere;
    private static Mesh unitQuad;
    private static Mesh unitCylinder;
    private static Mesh laserCylinder;
    private static Mesh fullscreenQuad;

    private SharedMeshes() {
    }

    public static void init() {
        if (sphere == null) {
            sphere = buildSphereMesh();
        }
        if (unitQuad == null) {
            unitQuad = buildUnitQuadMesh();
        }
        if (unitCylinder == null) {
            unitCylinder = buildUnitCylinderMesh();
        }
        if (laserCylinder == null) {
            laserCylinder = buildLaserCylinderMesh(64);
        }
        if (fullscreenQuad == null) {
            fullscreenQuad = buildFullscreenQuadMesh();
        }
    }

    public static Mesh sphere() {
        init();
        return sphere;
    }

    public static Mesh unitQuad() {
        init();
        return unitQuad;
    }

    public static Mesh unitCylinder() {
        init();
        return unitCylinder;
    }

    public static Mesh laserCylinder() {
        init();
        return laserCylinder;
    }

    public static Mesh fullscreenQuad() {
        init();
        return fullscreenQuad;
    }

    public static void close() throws Exception {
        closeMesh(sphere);
        closeMesh(unitQuad);
        closeMesh(unitCylinder);
        closeMesh(laserCylinder);
        closeMesh(fullscreenQuad);
        sphere = null;
        unitQuad = null;
        unitCylinder = null;
        laserCylinder = null;
        fullscreenQuad = null;
    }

    private static void closeMesh(Mesh mesh) throws Exception {
        if (mesh != null) {
            mesh.close();
        }
    }

    private static Mesh buildSphereMesh() {
        VertexLayout layout = new VertexLayout();
        layout
                .addNextAttribute(new VertexAttribute(0, 3, GL_FLOAT, false))
                .addNextAttribute(new VertexAttribute(1, 4, GL_UNSIGNED_BYTE, true))
                .addNextAttribute(new VertexAttribute(3, 3, GL_BYTE, true));

        MeshBuilder builder = new MeshBuilder();
        for (int i = 0; i < SPHERE_STACKS + 1; i++) {
            float phi = (float) Math.PI * i / SPHERE_STACKS;
            for (int j = 0; j < SPHERE_SLICES + 1; j++) {
                float theta = (float) (2 * Math.PI / SPHERE_SLICES * j);
                float x = (float) (SPHERE_RADIUS * Math.sin(phi) * Math.cos(theta));
                float y = (float) (SPHERE_RADIUS * Math.cos(phi));
                float z = (float) (SPHERE_RADIUS * Math.sin(phi) * Math.sin(theta));
                builder.pos(x, y, z).color(0.0f, 1.0f, 1.0f, 1.0f).normal(x, y, z).endVertex();
            }
        }

        for (int i = 0; i < SPHERE_STACKS; i++) {
            for (int j = 0; j < SPHERE_SLICES; j++) {
                int i0 = j + i * (SPHERE_SLICES + 1);
                int i1 = j + (i + 1) * (SPHERE_SLICES + 1);
                int i2 = j + 1 + (i + 1) * (SPHERE_SLICES + 1);
                int i3 = j + 1 + i * (SPHERE_SLICES + 1);
                builder.addTriangle(i0, i2, i1);
                builder.addTriangle(i0, i3, i2);
            }
        }

        ByteBuffer vertexData = builder.buildBuffer(layout);
        ShortBuffer indexBuffer = builder.buildIndexBufferU16();
        Mesh mesh = new Mesh(GL_TRIANGLES);
        mesh.uploadAndConfigure(vertexData, layout, GL_STATIC_DRAW, indexBuffer, GL_STATIC_DRAW);
        return mesh;
    }

    private static Mesh buildUnitQuadMesh() {
        VertexLayout layout = new VertexLayout();
        layout.addNextAttribute(new VertexAttribute(0, 3, GL_FLOAT, false));

        MeshBuilder builder = new MeshBuilder();
        builder.pos(-1.0f, 0.0f, -1.0f).endVertex()
                .pos(1.0f, 0.0f, -1.0f).endVertex()
                .pos(1.0f, 0.0f, 1.0f).endVertex()
                .pos(1.0f, 0.0f, 1.0f).endVertex()
                .pos(-1.0f, 0.0f, 1.0f).endVertex()
                .pos(-1.0f, 0.0f, -1.0f).endVertex();

        ByteBuffer vertexData = builder.buildBuffer(layout);
        Mesh mesh = new Mesh(GL_TRIANGLES);
        mesh.uploadAndConfigure(vertexData, layout, GL_STATIC_DRAW);
        return mesh;
    }

    private static Mesh buildUnitCylinderMesh() {
        VertexLayout layout = new VertexLayout();
        layout
                .addNextAttribute(new VertexAttribute(0, 3, GL_FLOAT, false))
                .addNextAttribute(new VertexAttribute(1, 4, GL_UNSIGNED_BYTE, true))
                .addNextAttribute(new VertexAttribute(3, 3, GL_BYTE, true));

        MeshBuilder builder = new MeshBuilder();
        float step = (float) (2.0 * Math.PI / UNIT_CYLINDER_SIDE_QUADS);
        for (int i = 0; i < UNIT_CYLINDER_SIDE_QUADS; i++) {
            float theta0 = step * i;
            float theta1 = step * (i + 1);
            float normalTheta = theta0 + step * 0.5f;

            float x0 = (float) Math.cos(theta0) * UNIT_CYLINDER_RADIUS;
            float z0 = (float) Math.sin(theta0) * UNIT_CYLINDER_RADIUS;
            float x1 = (float) Math.cos(theta1) * UNIT_CYLINDER_RADIUS;
            float z1 = (float) Math.sin(theta1) * UNIT_CYLINDER_RADIUS;
            float nx = (float) Math.cos(normalTheta);
            float nz = (float) Math.sin(normalTheta);

            int base = i * 4;
            builder.pos(x0, 0.0f, z0).color(0.0f, 1.0f, 1.0f, 1.0f).normal(nx, 0.0f, nz).endVertex()
                    .pos(x0, UNIT_CYLINDER_HEIGHT, z0).color(0.0f, 1.0f, 1.0f, 1.0f).normal(nx, 0.0f, nz).endVertex()
                    .pos(x1, UNIT_CYLINDER_HEIGHT, z1).color(0.0f, 1.0f, 1.0f, 1.0f).normal(nx, 0.0f, nz).endVertex()
                    .pos(x1, 0.0f, z1).color(0.0f, 1.0f, 1.0f, 1.0f).normal(nx, 0.0f, nz).endVertex();
            builder.addQuadIndices(base);
        }

        ByteBuffer vertexData = builder.buildBuffer(layout);
        ShortBuffer indexBuffer = builder.buildIndexBufferU16();
        Mesh mesh = new Mesh(GL_TRIANGLES);
        mesh.uploadAndConfigure(vertexData, layout, GL_STATIC_DRAW, indexBuffer, GL_STATIC_DRAW);
        return mesh;
    }

    private static Mesh buildLaserCylinderMesh(final int sideQuads) {
        VertexLayout layout = new VertexLayout();
        layout
                .addNextAttribute(new VertexAttribute(0, 3, GL_FLOAT, false))
                .addNextAttribute(new VertexAttribute(2, 2, GL_FLOAT, false))
                .addNextAttribute(new VertexAttribute(3, 3, GL_BYTE, true));

        MeshBuilder builder = new MeshBuilder();
        float step = (float) (2.0 * Math.PI / sideQuads);
        for (int i = 0; i < sideQuads; i++) {
            float theta0 = step * i;
            float theta1 = step * (i + 1);
            float normalTheta = theta0 + step * 0.5f;
            float u0 = (float) i / sideQuads;
            float u1 = (float) (i + 1) / sideQuads;

            float x0 = (float) Math.cos(theta0) * UNIT_CYLINDER_RADIUS;
            float z0 = (float) Math.sin(theta0) * UNIT_CYLINDER_RADIUS;
            float x1 = (float) Math.cos(theta1) * UNIT_CYLINDER_RADIUS;
            float z1 = (float) Math.sin(theta1) * UNIT_CYLINDER_RADIUS;
            float nx = (float) Math.cos(normalTheta);
            float nz = (float) Math.sin(normalTheta);

            int base = i * 4;
            builder.pos(x0, 0.0f, z0).uv(u0, 0.0f).normal(nx, 0.0f, nz).endVertex()
                    .pos(x0, UNIT_CYLINDER_HEIGHT, z0).uv(u0, 1.0f).normal(nx, 0.0f, nz).endVertex()
                    .pos(x1, UNIT_CYLINDER_HEIGHT, z1).uv(u1, 1.0f).normal(nx, 0.0f, nz).endVertex()
                    .pos(x1, 0.0f, z1).uv(u1, 0.0f).normal(nx, 0.0f, nz).endVertex();
            builder.addQuadIndices(base);
        }

        ByteBuffer vertexData = builder.buildBuffer(layout);
        ShortBuffer indexBuffer = builder.buildIndexBufferU16();
        Mesh mesh = new Mesh(GL_TRIANGLES);
        mesh.uploadAndConfigure(vertexData, layout, GL_STATIC_DRAW, indexBuffer, GL_STATIC_DRAW);
        return mesh;
    }

    private static Mesh buildFullscreenQuadMesh() {
        MeshBuilder builder = new MeshBuilder();
        builder.pos(-1.0f, 1.0f, 0.0f).uv(0.0f, 1.0f).endVertex();
        builder.pos(-1.0f, -1.0f, 0.0f).uv(0.0f, 0.0f).endVertex();
        builder.pos(1.0f, -1.0f, 0.0f).uv(1.0f, 0.0f).endVertex();
        builder.pos(1.0f, 1.0f, 0.0f).uv(1.0f, 1.0f).endVertex();
        builder.addTriangle(0, 1, 2);
        builder.addTriangle(0, 2, 3);

        VertexLayout layout = getPostProcessingLayout();
        ByteBuffer vertexBuffer = builder.buildBuffer(layout);
        ShortBuffer indexBuffer = builder.buildIndexBufferU16();

        Mesh mesh = new Mesh(GL_TRIANGLES);
        mesh.uploadAndConfigure(vertexBuffer, layout, GL_STATIC_DRAW, indexBuffer, GL_STATIC_DRAW);
        return mesh;
    }

    public static VertexLayout getPostProcessingLayout() {
        VertexLayout vertexLayout = new VertexLayout();
        vertexLayout.addAttribute(VertexAttributeEnum.POSITION.get());
        vertexLayout.addAttribute(VertexAttributeEnum.UV.get());
        return vertexLayout;
    }
}
