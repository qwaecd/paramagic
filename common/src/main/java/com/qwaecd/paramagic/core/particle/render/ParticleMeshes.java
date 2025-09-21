package com.qwaecd.paramagic.core.particle.render;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.render.vertex.Mesh;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttribute;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;

public class ParticleMeshes {
    private static Mesh UNIT_QUAD;
    private static Mesh UNIT_TRIANGLE;

    private static boolean isInitialized = false;

    public static void init() {
        if (isInitialized) {
            Paramagic.LOG.warn("ParticleMeshes is already initialized. Skipping.");
            return;
        }

        VertexLayout particleVertexLayout = new VertexLayout();
        particleVertexLayout.addAttribute(new VertexAttribute(2, 2, GL_FLOAT, false));
        buildQuad(particleVertexLayout);
        buildTriangle(particleVertexLayout);

        isInitialized = true;
    }
    /**
     * Retrieves a pre-built standard particle mesh.
     * @param type The desired mesh shape.
     * @return The requested shared Mesh object.
     */
    public static Mesh get(ParticleMeshType type) {
        if (!isInitialized) {
            throw new IllegalStateException("ParticleMeshes has not been initialized. Call ParticleManager.init() first.");
        }
        return switch (type) {
            case QUAD -> UNIT_QUAD;
            case TRIANGLE -> UNIT_TRIANGLE;
        };
    }

    private static void buildQuad(VertexLayout particleVertexLayout) {


        UNIT_QUAD = new Mesh(GL_TRIANGLES);
        MeshBuilder quadBuilder = new MeshBuilder();
        ByteBuffer quadBuffer = quadBuilder
                .pos(-0.5f, -0.5f, 0).endVertex()
                .pos( 0.5f, -0.5f, 0).endVertex()
                .pos( 0.5f,  0.5f, 0).endVertex()
                .pos(-0.5f,  0.5f, 0).endVertex()
                .addQuadIndices(0)
                .buildBuffer(particleVertexLayout);
        UNIT_QUAD.uploadAndConfigure(quadBuffer, particleVertexLayout, GL_STATIC_DRAW);
    }

    private static void buildTriangle(VertexLayout particleVertexLayout) {
        UNIT_TRIANGLE = new Mesh(GL_TRIANGLES);
        MeshBuilder triangleBuilder = new MeshBuilder();
        float height = (float) (Math.sqrt(3.0) / 2.0);
        float halfHeight = height / 2.0f;
        ByteBuffer triangleBuffer = triangleBuilder
                .pos( 0.5f,     0.0f, 0).endVertex()
                .pos(-0.5f, -halfHeight, 0).endVertex()
                .pos(-0.5f,  halfHeight, 0).endVertex()
                .addTriangle(0, 1, 2)
                .buildBuffer(particleVertexLayout);
        UNIT_TRIANGLE.uploadAndConfigure(triangleBuffer, particleVertexLayout, GL_STATIC_DRAW);
    }

    public enum ParticleMeshType {
        QUAD,
        TRIANGLE
    }
}
