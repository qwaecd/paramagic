package com.qwaecd.paramagic.core.particle.request;

import com.qwaecd.paramagic.core.particle.compute.ComputeShader;
import com.qwaecd.paramagic.core.particle.compute.IComputeShaderProvider;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.memory.ParticleMemoryManager;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL42.GL_ATOMIC_COUNTER_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BARRIER_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;


public class ParticleEmissionProcessor {
    private final IComputeShaderProvider shaderProvider;

    // 用于缓存的 buffer
    private final ByteBuffer stagingBuffer;

    public ParticleEmissionProcessor(IComputeShaderProvider shaderProvider, int maxRequestsPreFrame) {
        this.shaderProvider = shaderProvider;
        this.stagingBuffer = MemoryUtil.memAlloc(EmissionRequest.SIZE_IN_BYTES * maxRequestsPreFrame).order(ByteOrder.nativeOrder());
    }

    public void reserveParticles(int requestCount, List<EmissionRequest> reqs, ParticleMemoryManager memoryManager) {
        memoryManager.bindMainBuffers();

        ComputeShader reserveRequestShader = this.shaderProvider.reserveRequestShader();
        reserveRequestShader.bind();
        reserveRequestShader.setUniformValue1i("u_requestCount", requestCount);

        memoryManager.reserveStep();
        this.stagingBuffer.clear();
        for (int i = 0; i < requestCount; i++) {
            EmissionRequest req = reqs.get(i);
            req.writeToBuffer(this.stagingBuffer);
        }
        this.stagingBuffer.flip();
        // 在 memoryManager.reserveStep(); 内已经绑定了对应的 GL_SHADER_STORAGE_BUFFER
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, this.stagingBuffer);

        // 派发数量固定为 1
        reserveRequestShader.dispatch(1, 1, 1);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT | GL_ATOMIC_COUNTER_BARRIER_BIT);
        reserveRequestShader.unbind();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void initializeParticles(int requestCount, ParticleMemoryManager memoryManager) {
        memoryManager.initializeParticleStep();
        ComputeShader initializeRequestShader = this.shaderProvider.initializeRequestShader();

        initializeRequestShader.bind();
        initializeRequestShader.dispatch(requestCount, 1, 1);

        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);

        initializeRequestShader.unbind();
    }
}
