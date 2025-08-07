package com.qwaecd.paramagic.core.render.vertex;

import static org.lwjgl.opengl.GL33.*;

/**
 * Creates a new VertexAttribute.
 *
 * @param location The index of the attribute in the shader.
 * @param size The number of components per vertex attribute (e.g., 3 for a vec3).
 * @param type The data type of the attribute (e.g., GL_FLOAT).
 * @param normalized Whether the data should be normalized.
 */
public record VertexAttribute (int location, int size, int type, boolean normalized) {
    public int getTypeSize() {
        return switch (type) {
            case GL_FLOAT -> Float.BYTES;
            case GL_DOUBLE -> Double.BYTES;
            case GL_UNSIGNED_INT, GL_INT -> Integer.BYTES;
            case GL_UNSIGNED_BYTE, GL_BYTE -> Byte.BYTES;
            case GL_SHORT, GL_UNSIGNED_SHORT -> Short.BYTES;
            default -> 0;
        };
    }
}
