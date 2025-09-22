package com.qwaecd.paramagic.core.particle.data;

import com.qwaecd.paramagic.core.render.vertex.Mesh;

public class ParticleMesh extends Mesh {
    private final ParticleMeshType type;
    public ParticleMesh(int drawMode) {
        super(drawMode);
        this.type = ParticleMeshType.QUAD;
    }

    public ParticleMesh(int drawMode, ParticleMeshType type) {
        super(drawMode);
        this.type = type;
    }

    public enum ParticleMeshType {
        QUAD,
        TRIANGLE
    }

    public int getIndexCount() {
        // Return the element (index) count for GL_TRIANGLES
        return switch (this.type) {
            case QUAD -> 6;      // two triangles: 0,1,2, 0,2,3
            case TRIANGLE -> 3;  // one triangle: 0,1,2
        };
    }
}
