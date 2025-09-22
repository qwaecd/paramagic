package com.qwaecd.paramagic.core.particle.data;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.render.vertex.MeshBuilder;
import com.qwaecd.paramagic.core.render.vertex.VertexAttribute;
import com.qwaecd.paramagic.core.render.vertex.VertexLayout;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;

public class ParticleMeshes {
    private static ParticleMesh UNIT_QUAD;
    private static ParticleMesh UNIT_TRIANGLE;

    // Dedicated index buffers (EBO) per mesh type, using U32 to match renderer's GL_UNSIGNED_INT
    private static int QUAD_EBO = 0;
    private static int TRIANGLE_EBO = 0;

    private static boolean isInitialized = false;

    public static void init() {
        if (isInitialized) {
            Paramagic.LOG.warn("ParticleMeshes is already initialized. Skipping.");
            return;
        }

        // Build position-only layout: location 0 as vec3 (we'll bind it as attrib 10 on the instanced VAO)
        VertexLayout positionOnlyLayout = new VertexLayout();
        positionOnlyLayout.addAttribute(new VertexAttribute(0, 3, GL_FLOAT, false));

        buildQuad(positionOnlyLayout);
        buildTriangle(positionOnlyLayout);

        isInitialized = true;
    }

    /**
     * Retrieves a pre-built standard particle mesh.
     * Also binds the corresponding EBO to the currently bound VAO to support glDrawElementsInstanced.
     * @param type The desired mesh shape.
     * @return The requested shared Mesh object.
     */
    public static ParticleMesh get(ParticleMesh.ParticleMeshType type) {
        if (!isInitialized) {
            throw new IllegalStateException("ParticleMeshes has not been initialized. Call ParticleManager.init() first.");
        }

        // Bind the correct EBO to the currently bound VAO. This is required for glDrawElementsInstanced.
        switch (type) {
            case QUAD -> {
                if (QUAD_EBO == 0) {
                    Paramagic.LOG.error("QUAD EBO is not initialized.");
                } else {
                    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, QUAD_EBO);
                }
                return UNIT_QUAD;
            }
            case TRIANGLE -> {
                if (TRIANGLE_EBO == 0) {
                    Paramagic.LOG.error("TRIANGLE EBO is not initialized.");
                } else {
                    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, TRIANGLE_EBO);
                }
                return UNIT_TRIANGLE;
            }
            default -> throw new IllegalStateException("Unexpected particle mesh type: " + type);
        }
    }

    private static void buildQuad(VertexLayout positionOnlyLayout) {
        UNIT_QUAD = new ParticleMesh(GL_TRIANGLES, ParticleMesh.ParticleMeshType.QUAD);

        MeshBuilder quadBuilder = new MeshBuilder();
        ByteBuffer vertexBuffer = quadBuilder
                .pos(-0.5f, -0.5f, 0).endVertex()
                .pos( 0.5f, -0.5f, 0).endVertex()
                .pos( 0.5f,  0.5f, 0).endVertex()
                .pos(-0.5f,  0.5f, 0).endVertex()
                .addQuadIndices(0) // indices: 0,1,2, 0,2,3
                .buildBuffer(positionOnlyLayout);

        // Upload vertex buffer only; we'll manage EBO separately so it can be bound to the instanced VAO at draw time.
        UNIT_QUAD.uploadAndConfigure(vertexBuffer, positionOnlyLayout, GL_STATIC_DRAW);

        // Create a dedicated EBO with U32 indices for QUAD
        IntBuffer indexU32 = new MeshBuilder()
                .addQuadIndices(0)
                .buildIndexBufferU32();
        if (QUAD_EBO == 0) QUAD_EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, QUAD_EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexU32, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private static void buildTriangle(VertexLayout positionOnlyLayout) {
        UNIT_TRIANGLE = new ParticleMesh(GL_TRIANGLES, ParticleMesh.ParticleMeshType.TRIANGLE);

        MeshBuilder triangleBuilder = new MeshBuilder();
        float height = (float) (Math.sqrt(3.0) / 2.0);
        float halfHeight = height / 2.0f;
        ByteBuffer vertexBuffer = triangleBuilder
                .pos( 0.5f,     0.0f, 0).endVertex()
                .pos(-0.5f, -halfHeight, 0).endVertex()
                .pos(-0.5f,  halfHeight, 0).endVertex()
                .addTriangle(0, 1, 2)
                .buildBuffer(positionOnlyLayout);

        // Upload vertex buffer only
        UNIT_TRIANGLE.uploadAndConfigure(vertexBuffer, positionOnlyLayout, GL_STATIC_DRAW);

        // Create a dedicated EBO with U32 indices for TRIANGLE
        IntBuffer indexU32 = new MeshBuilder()
                .addTriangle(0, 1, 2)
                .buildIndexBufferU32();
        if (TRIANGLE_EBO == 0) TRIANGLE_EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, TRIANGLE_EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexU32, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
