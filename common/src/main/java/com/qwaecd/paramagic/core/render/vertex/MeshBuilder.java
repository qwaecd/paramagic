package com.qwaecd.paramagic.core.render.vertex;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class MeshBuilder {
    private final List<Vertex> vertices = new ArrayList<>();
    private Vertex.Builder currentVertex = new Vertex.Builder();

    public MeshBuilder pos(float x, float y, float z) {
        currentVertex.pos(x, y, z);
        return this;
    }

    public MeshBuilder color(float r, float g, float b, float a) {
        currentVertex.color(r, g, b, a);
        return this;
    }

    public MeshBuilder color(Color color) {
        currentVertex.color(color);
        return this;
    }

    public MeshBuilder uv(float u, float v) {
        currentVertex.uv(u, v);
        return this;
    }

    public MeshBuilder normal(float nx, float ny, float nz) {
        currentVertex.normal(nx, ny, nz);
        return this;
    }

    /**
     * Finalize current vertex and add it to the mesh.
     */
    public MeshBuilder endVertex() {
        vertices.add(currentVertex.build());
        currentVertex = new Vertex.Builder();
        return this;
    }

    /**
     * Packs all constructed vertices into a compact ByteBuffer, strictly following the provided layout.
     * @param layout The VertexLayout that defines the structure of the data to be packed.
     * @return A ByteBuffer ready to upload to a VBO.
     */
    public ByteBuffer buildBuffer(VertexLayout layout) {
        if (layout == null || layout.getStride() == 0) {
            throw new IllegalArgumentException("A valid VertexLayout is required to build the buffer.");
        }

        int stride = layout.getStride();
        ByteBuffer buffer = BufferUtils.createByteBuffer(vertices.size() * stride);
        buffer.order(ByteOrder.nativeOrder());

        for (Vertex v : vertices) {
            for (VertexAttribute attribute : layout.getAttributes()) {
                writeBytes(buffer, v, attribute);
            }
        }

        return buffer.flip();
    }

    private void writeBytes(ByteBuffer buffer ,Vertex v, VertexAttribute attribute) {
        switch (attribute.location()) {
            case 0: // 位置
                if (attribute.type() == GL_FLOAT && attribute.size() == 3) {
                    buffer.putFloat(v.getX());
                    buffer.putFloat(v.getY());
                    buffer.putFloat(v.getZ());
                } else {
                    throw new IllegalArgumentException("Layout attribute at location 0 must be of type vec3 (3 floats).");
                }
                break;
            case 1: // 颜色
                if (attribute.type() == GL_UNSIGNED_BYTE && attribute.size() == 4 && attribute.normalized()) {
                    buffer.put((byte) (v.getR() * 255));
                    buffer.put((byte) (v.getG() * 255));
                    buffer.put((byte) (v.getB() * 255));
                    buffer.put((byte) (v.getA() * 255));
                } else {
                    throw new IllegalArgumentException("Layout attribute at location 1 must be a normalized vec4 of unsigned bytes.");
                }
                break;
            case 2: // UV
                if (attribute.type() == GL_FLOAT && attribute.size() == 2) {
                    buffer.putFloat(v.getU());
                    buffer.putFloat(v.getV());
                } else {
                    throw new IllegalArgumentException("Layout attribute at location 2 must be of type vec2 (2 floats).");
                }
                break;
            case 3: // 法线
                if (attribute.type() == GL_BYTE && attribute.size() == 3 && attribute.normalized()) {
                    Vector3f normal = v.getNormal();
                    buffer.put((byte) (normal.x * 127));
                    buffer.put((byte) (normal.y * 127));
                    buffer.put((byte) (normal.z * 127));
                } else {
                    throw new IllegalArgumentException("Layout attribute at location 3 must be a normalized vec3 of bytes.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported attribute location in convention: " + attribute.location());
        }
    }
}