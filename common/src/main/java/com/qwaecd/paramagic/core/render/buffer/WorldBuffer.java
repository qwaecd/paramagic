package com.qwaecd.paramagic.core.render.buffer;

import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.vertex.Vertex;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class WorldBuffer {
    private final List<Vertex> vertices = new ArrayList<>();

    private final int drawMode;

    private final Shader shader;
    private FloatBuffer projectionMatrix;
    private final Vector3d cameraPosition;

    public WorldBuffer(int drawMode, Shader shader, Matrix4f viewMatrix, RenderContext context) {
        this.drawMode = drawMode;
        this.shader = shader;
        this.cameraPosition = context.getCamera().position();
        makeProjectionMatrix(context.getProjectionMatrix(), viewMatrix);
    }

    public void vert(float x, float y, float z, float r, float g, float b, float a) {
        vertices.add(new Vertex(
                x - (float) cameraPosition.x,
                y - (float) cameraPosition.y,
                z - (float) cameraPosition.z,
                r, g, b, a
        ));
    }

    public void draw() {
        BufferManager.bind();
        BufferManager.bindBuffer();

        BufferManager.writeBuffer(getBuffer());
        applyProjectionMatrix();

        int stride = 7 * 4; // 7 floats (3 for pos, 4 for color), each 4 bytes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, stride, 3 * 4L);
        glEnableVertexAttribArray(1);

        BufferManager.unbindBuffer();

        shader.bind();
        BufferManager.draw(drawMode, vertices.size());
        shader.unbind();

        BufferManager.unbind();
    }

    private FloatBuffer getBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.size() * 7);
        ArrayList<Float> floats = new ArrayList<>();
        for (Vertex vertex : vertices) {
            floats.add(vertex.getX());
            floats.add(vertex.getY());
            floats.add(vertex.getZ());
            floats.add(vertex.getR());
            floats.add(vertex.getG());
            floats.add(vertex.getB());
            floats.add(vertex.getA());
        }
        Float[] floatArray = new Float[floats.size()];
        floats.toArray(floatArray);
        buffer.put(ArrayUtils.toPrimitive(floatArray));
        return buffer.flip();
    }

    private void makeProjectionMatrix(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        Matrix4f modelViewProjectionMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix);
        this.projectionMatrix = modelViewProjectionMatrix.get(BufferUtils.createFloatBuffer(16));

        shader.setUniformMatrix4f("u_projection", projectionMatrix);
        shader.setUniformMatrix4f("u_view", viewMatrix);
        shader.setUniformMatrix4f("u_model", new Matrix4f().identity());
    }

    private void applyProjectionMatrix() {
//        shader.uniformMatrix4f("u_projection", projectionMatrix);
    }
}
