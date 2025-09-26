package com.qwaecd.paramagic.core.particle.compute;

import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL43.*;

public class ComputeParticleDemo {
    private static ComputeParticleDemo INSTANCE;
    private final int NUM_PARTICLES;
    private final int LOCAL_SIZE = 256; // 与 shader 中 layout(local_size_x = 256) 对齐
    private final Shader computeShader;   // compute shader program id
    private final Shader renderShader;    // render shader program id
    // 初始化半径（粒子会均匀分布在以 (0,0,0) 为中心、该半径的球体内部）
    private final float INIT_RADIUS = 10f;
    // 位置整体 Y 偏移，便于在场景中观察
    private final float INIT_Y_OFFSET = 100f;
    // 初始最大速度（标量）。速度模长将采样于 [0, MAX_INIT_SPEED]，方向各向同性
    private final float MAX_INIT_SPEED = 0.1f;

    // GL 对象
    private int ssboPositions;
    private int ssboVelocities;
    private int vao;
    public ComputeParticleDemo() {
        this.NUM_PARTICLES = 500_000;
        this.computeShader = ShaderManager.getInstance().getShaderThrowIfNotFound("compute_demo");
        this.renderShader = ShaderManager.getInstance().getShaderThrowIfNotFound("compute_render");
    }

    public static ComputeParticleDemo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComputeParticleDemo();
            INSTANCE.init();
        }
        return INSTANCE;
    }

    public void init() {
        createSSBOs();
        createVAOForDraw();
        // Enable program point size so gl_PointSize in shader takes effect
        glEnable(GL_PROGRAM_POINT_SIZE);
    }
    public void updateAndRender(float deltaTime, RenderContext context) {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssboPositions);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, ssboVelocities);

        this.computeShader.bind();
        this.computeShader.setUniformValue1i("u_numParticles", NUM_PARTICLES);
        this.computeShader.setUniformValue1f("u_deltaTime", deltaTime);
        this.computeShader.setUniformValue3f("u_gravity", 0.0f, -0.00001f, 0.0f);

        int numGroups = (NUM_PARTICLES + LOCAL_SIZE - 1) / LOCAL_SIZE;
        glDispatchCompute(numGroups, 1, 1);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT | GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT);

        render(context);

        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, 0);
    }

    private void render(RenderContext context) {
        this.renderShader.bind();
        glBindVertexArray(vao);
        Matrix4f projectionMatrix = context.getProjectionMatrix();
        Matrix4f viewMatrix = context.getMatrixStackProvider().getViewMatrix();
        Vector3d cameraPos = context.getCamera().position();
        // Pass uniforms to compute_render.vsh
        this.renderShader.setUniformMatrix4f("u_projection", projectionMatrix);
        this.renderShader.setUniformMatrix4f("u_view", viewMatrix);
        this.renderShader.setUniformValue3f("u_cameraPos", (float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);

        glDrawArrays(GL_POINTS, 0, NUM_PARTICLES);
        glBindVertexArray(0);
        glUseProgram(0);
    }

    private void createSSBOs() {
        // 每个粒子用 vec4 存位置（x,y,z,w）和 vec4 存速度
        final int floatsPerParticle = 4;
        final long posBytes = (long) NUM_PARTICLES * floatsPerParticle * Float.BYTES;
        final long velBytes = (long) NUM_PARTICLES * floatsPerParticle * Float.BYTES;

        ssboPositions = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssboPositions);
        glBufferData(GL_SHADER_STORAGE_BUFFER, posBytes, GL_DYNAMIC_DRAW);

        FloatBuffer posBuffer = MemoryUtil.memAllocFloat(NUM_PARTICLES * floatsPerParticle);
        Random rand = new Random(12345);
        // 粒子均匀分布在球体内部：使用球坐标 + r ~ cbrt(U) 使体积均匀
        for (int i = 0; i < NUM_PARTICLES; i++) {
            // 生成三个独立随机数
            float u1 = rand.nextFloat(); // 用于 theta
            float u2 = rand.nextFloat(); // 用于 phi（通过 cos(phi) = 2u2 - 1 保证表面均匀）
            float u3 = rand.nextFloat(); // 用于半径（cbrt 使体积均匀）

            float theta = (float) (2.0 * Math.PI * u1);
            float cosPhi = 2.0f * u2 - 1.0f; // cos(phi) ∈ [-1,1]
            float sinPhi = (float) Math.sqrt(1.0f - cosPhi * cosPhi);
            float radius = INIT_RADIUS * (float) Math.cbrt(u3);

            float x = radius * sinPhi * (float) Math.cos(theta);
            float y = radius * cosPhi + INIT_Y_OFFSET; // 平移到偏移高度
            float z = radius * sinPhi * (float) Math.sin(theta);

            posBuffer.put(x).put(y).put(z).put(1.0f); // w = 1.0 (unused)
        }
        posBuffer.flip();
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, posBuffer);
        MemoryUtil.memFree(posBuffer);

        // 生成 SSBO（velocities）
        ssboVelocities = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssboVelocities);
        glBufferData(GL_SHADER_STORAGE_BUFFER, velBytes, GL_DYNAMIC_DRAW);

        // 初始化速度：各向同性随机方向 + 随机模长（使用 cbrt 让速度在低值有更高密度，避免过快发散）
        FloatBuffer velBuffer = MemoryUtil.memAllocFloat(NUM_PARTICLES * floatsPerParticle);
        for (int i = 0; i < NUM_PARTICLES; i++) {
            // 随机方向（单位向量）
//            float du1 = rand.nextFloat();
//            float du2 = rand.nextFloat();
//            float thetaDir = (float) (2.0 * Math.PI * du1);
//            float cosPhiDir = 2.0f * du2 - 1.0f;
//            float sinPhiDir = (float) Math.sqrt(1.0f - cosPhiDir * cosPhiDir);
//            float dirX = sinPhiDir * (float) Math.cos(thetaDir);
//            float dirY = cosPhiDir;
//            float dirZ = sinPhiDir * (float) Math.sin(thetaDir);
//            // 速度模长：让更小速度概率更大（cbrt）
//            float speed = MAX_INIT_SPEED * (float) Math.cbrt(rand.nextFloat());
//            velBuffer.put(dirX * speed).put(dirY * speed).put(dirZ * speed).put(0.0f);
            velBuffer.put(-0.05f).put(0.0f).put(0.01f).put(0.0f);
        }
        velBuffer.flip();
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, velBuffer);
        MemoryUtil.memFree(velBuffer);

        // 解绑
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    private void createVAOForDraw() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        // 无需设置 glEnableVertexAttribArray，因为顶点数据从 SSBO 读取（顶点着色器使用 gl_VertexID）
        glBindVertexArray(0);
    }
}
