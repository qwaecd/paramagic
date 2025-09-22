package com.qwaecd.paramagic.core.particle.data;


import static org.lwjgl.opengl.GL33.*;

public class InstancedParticleVAO {
    private final int vao;
    private int configuredVboId = -1;


    public InstancedParticleVAO() {
        this.vao = glGenVertexArrays();
    }

    public void bindAndConfigure(int simulationVBO) {
        glBindVertexArray(this.vao);
        if (configuredVboId != simulationVBO) {
            glBindBuffer(GL_ARRAY_BUFFER, simulationVBO);
            configSimulationAttributes();
            configMeshAttributes();
            configuredVboId = simulationVBO;
        }
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    private static void configSimulationAttributes() {
        final int stride = GPUParticle.SIZE_IN_BYTES;

        long offset = 0;

        // 属性0: vec3 position
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, offset);
        glVertexAttribDivisor(0, 1);
        offset += 3 * 4; // 12 bytes

        // 属性1: vec3 velocity
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, offset);
        glVertexAttribDivisor(1, 1);
        offset += 3 * 4; // 12 bytes

        // 属性2: float age
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, stride, offset);
        glVertexAttribDivisor(2, 1);
        offset += 4; // 4 bytes

        // 属性3: float lifetime
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 1, GL_FLOAT, false, stride, offset);
        glVertexAttribDivisor(3, 1);
        offset += 4; // 4 bytes

        // 属性4: vec4 color
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, stride, offset);
        glVertexAttribDivisor(4, 1);
        offset += 4 * 4; // 16 bytes

        // 属性5: float intensity
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 1, GL_FLOAT, false, stride, offset);
        glVertexAttribDivisor(5, 1);
        offset += 4; // 4 bytes

        // 属性6: float size
        glEnableVertexAttribArray(6);
        glVertexAttribPointer(6, 1, GL_FLOAT, false, stride, offset);
        glVertexAttribDivisor(6, 1);
        offset += 4; // 4 bytes

        // 属性7: float angle
        glEnableVertexAttribArray(7);
        glVertexAttribPointer(7, 1, GL_FLOAT, false, stride, offset);
        glVertexAttribDivisor(7, 1);
        offset += 4; // 4 bytes

        // 属性8: float angularVelocity
        glEnableVertexAttribArray(8);
        glVertexAttribPointer(8, 1, GL_FLOAT, false, stride, offset);
        glVertexAttribDivisor(8, 1);
        offset += 4; // 4 bytes

        // 属性9: int index
        glEnableVertexAttribArray(9);
        glVertexAttribIPointer(9, 1, GL_INT, stride, offset);
        glVertexAttribDivisor(9, 1);
    }

    private static void configMeshAttributes() {
        final int meshStride = 3 * Float.BYTES;   // 一个vec3顶点的大小
        final long meshOffset = 0;

        // TODO: 目前只支持QUAD，后续可以扩展
        ParticleMesh mesh = ParticleMeshes.get(ParticleMesh.ParticleMeshType.QUAD);
        int meshVBO = mesh.getVBO();
        glBindBuffer(GL_ARRAY_BUFFER, meshVBO);
        glEnableVertexAttribArray(10);
        glVertexAttribPointer(10, 3, GL_FLOAT, false, meshStride, meshOffset);
        glVertexAttribDivisor(10, 0);
    }
}
