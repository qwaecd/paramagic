package com.qwaecd.paramagic.core.render.vertex;

import static org.lwjgl.opengl.GL33.*;

public enum VertexAttributeEnum {
    POSITION(0, 3, GL_FLOAT, false),
    COLOR(1, 4, GL_UNSIGNED_BYTE, true),
    HDR_COLOR(1, 4, GL_FLOAT, false),
    UV(2, 2, GL_FLOAT, false),
    NORMAL(3, 3, GL_BYTE, true);

    private final VertexAttribute attribute;

    VertexAttributeEnum(int location, int size, int type, boolean normalized) {
        this.attribute = new VertexAttribute(location, size, type, normalized);
    }

    public VertexAttribute get() {
        return attribute;
    }
}
