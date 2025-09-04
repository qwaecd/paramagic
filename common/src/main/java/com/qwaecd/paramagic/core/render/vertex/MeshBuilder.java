package com.qwaecd.paramagic.core.render.vertex;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class MeshBuilder {
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Integer> indices = new ArrayList<>();
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

    /**
     * GL_TRIANGLES
     */
    public MeshBuilder addTriangle(int i0, int i1, int i2) {
        indices.add(i0);
        indices.add(i1);
        indices.add(i2);
        return this;
    }

    /**
     * 添加一个四边形（两个三角）：(base, base+1, base+2, base, base+2, base+3)
     */
    public MeshBuilder addQuadIndices(int base) {
        indices.add(base);
        indices.add(base + 1);
        indices.add(base + 2);
        indices.add(base);
        indices.add(base + 2);
        indices.add(base + 3);
        return this;
    }

    public ShortBuffer buildIndexBufferU16() {
        for (int idx : indices) {
            if (idx < 0 || idx > 0xFFFF) {
                throw new IllegalStateException("Index " + idx + " exceeds 16-bit range. Use U32 buffer instead.");
            }
        }
        ShortBuffer buf = BufferUtils.createShortBuffer(indices.size());
        for (int idx : indices) buf.put((short) (idx & 0xFFFF));
        return buf.flip();
    }

    public IntBuffer buildIndexBufferU32() {
        IntBuffer buf = BufferUtils.createIntBuffer(indices.size());
        for (int idx : indices) buf.put(idx);
        return buf.flip();
    }

    public int getVertexCount() {
        return vertices.size();
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
                    buffer.put((byte) (Math.round(Math.min(1.0f, Math.max(0.0f, v.getR())) * 255.0f) & 0xFF));
                    buffer.put((byte) (Math.round(Math.min(1.0f, Math.max(0.0f, v.getG())) * 255.0f) & 0xFF));
                    buffer.put((byte) (Math.round(Math.min(1.0f, Math.max(0.0f, v.getB())) * 255.0f) & 0xFF));
                    buffer.put((byte) (Math.round(Math.min(1.0f, Math.max(0.0f, v.getA())) * 255.0f) & 0xFF));
                } else if (attribute.type() == GL_FLOAT && attribute.size() == 4 && !attribute.normalized()) {
                    buffer.putFloat(v.getR());
                    buffer.putFloat(v.getG());
                    buffer.putFloat(v.getB());
                    buffer.putFloat(v.getA());
                } else {
                    throw new IllegalArgumentException("Layout attribute at location 1 must be a normalized vec4 of unsigned bytes, or floats.");
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