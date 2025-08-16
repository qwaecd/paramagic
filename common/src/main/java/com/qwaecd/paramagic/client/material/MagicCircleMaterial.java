package com.qwaecd.paramagic.client.material;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import org.joml.Vector4f;

public class MagicCircleMaterial extends AbstractMaterial {
    private final Vector4f baseColor = new Vector4f(0.3f, 0.8f, 1.0f, 1.0f);

    private static final int MAX_RINGS = 8;
    private int ringCount = 0;
    private final float[] ringR = new float[MAX_RINGS];
    private final float[] ringW = new float[MAX_RINGS];
    private final float[] ringI = new float[MAX_RINGS];

    // 预计算的uniform名称，避免每帧字符串拼接
    private static final String[] RING_R_UNIFORMS = new String[MAX_RINGS];
    private static final String[] RING_W_UNIFORMS = new String[MAX_RINGS];
    private static final String[] RING_I_UNIFORMS = new String[MAX_RINGS];

    static {
        for (int i = 0; i < MAX_RINGS; i++) {
            RING_R_UNIFORMS[i] = "u_ringR[" + i + "]";
            RING_W_UNIFORMS[i] = "u_ringW[" + i + "]";
            RING_I_UNIFORMS[i] = "u_ringI[" + i + "]";
        }
    }

    // 刻度/条纹/扫掠/核心
    private int sectors = 24;
    private float tickWidth = 0.02f;
    private float bandCount = 12.0f;
    private float bandWidth = 0.015f;
    private float sweepSpeed = 0.08f;
    private float sweepWidth = 0.03f;
    private float coreInner = 0.20f;

    public MagicCircleMaterial(Shader shader) {
        super(shader);
        addRing(0.95f, 0.012f, 0.9f);
        addRing(0.70f, 0.010f, 0.7f);
    }

    // 便捷配置 API
    public MagicCircleMaterial setColor(float r, float g, float b, float a) {
        baseColor.set(r, g, b, a);
        return this;
    }

    public MagicCircleMaterial clearRings() {
        ringCount = 0;
        return this;
    }

    public MagicCircleMaterial addRing(float radius, float width, float intensity) {
        if (ringCount < MAX_RINGS) {
            ringR[ringCount] = radius;
            ringW[ringCount] = width;
            ringI[ringCount] = intensity;
            ringCount++;
        }
        return this;
    }

    public MagicCircleMaterial setSectors(int s) {
        this.sectors = s;
        return this;
    }

    public MagicCircleMaterial setTickWidth(float w) {
        this.tickWidth = w;
        return this;
    }

    public MagicCircleMaterial setBand(float count, float width) {
        this.bandCount = count;
        this.bandWidth = width;
        return this;
    }

    public MagicCircleMaterial setSweep(float speed, float width) {
        this.sweepSpeed = speed;
        this.sweepWidth = width;
        return this;
    }

    public MagicCircleMaterial setCoreInner(float r) {
        this.coreInner = r;
        return this;
    }

    @Override
    public void applyCustomUniforms() {
        var sh = getShader();
        // 基色
        sh.setUniformValue4f("u_baseColor", baseColor.x, baseColor.y, baseColor.z, baseColor.w);

        sh.setUniformValue1i("u_ringCount", ringCount);
        for (int i = 0; i < ringCount; i++) {
            sh.setUniformValue1f(RING_R_UNIFORMS[i], ringR[i]);
            sh.setUniformValue1f(RING_W_UNIFORMS[i], ringW[i]);
            sh.setUniformValue1f(RING_I_UNIFORMS[i], ringI[i]);
        }

        // 刻度/条纹/扫掠/核心
        sh.setUniformValue1i("u_sectors", sectors);
        sh.setUniformValue1f("u_tickWidth", tickWidth);
        sh.setUniformValue1f("u_bandCount", bandCount);
        sh.setUniformValue1f("u_bandWidth", bandWidth);
        sh.setUniformValue1f("u_sweepSpeed", sweepSpeed);
        sh.setUniformValue1f("u_sweepWidth", sweepWidth);
        sh.setUniformValue1f("u_coreInner", coreInner);
    }

    @Override
    public RenderType getRenderType() {
        // 法阵大多是发光叠加，默认走加色模式
        return RenderType.ADDITIVE;
    }
}
