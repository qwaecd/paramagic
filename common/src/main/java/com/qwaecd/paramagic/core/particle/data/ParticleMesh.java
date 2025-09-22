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

    public int getVertexCount() {
        return switch (this.type) {
            case QUAD -> 4;
            case TRIANGLE -> 3;
        };
    }
}
