package com.qwaecd.paramagic.client.material;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import org.joml.Vector4f;

public class MagicCircleMaterial extends AbstractMaterial {
    private final Vector4f baseColor = new Vector4f(0.3f, 0.8f, 1.0f, 1.0f);

    // 环参数（最多 8 条）
    private static final int MAX_RINGS = 8;
    private int ringCount = 0;
    private final float[] ringR = new float[MAX_RINGS];
    private final float[] ringW = new float[MAX_RINGS];
    private final float[] ringI = new float[MAX_RINGS];

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
        // 给两个默认环，开箱即用
        addRing(0.95f, 0.012f, 0.9f);
        addRing(0.70f, 0.010f, 0.7f);
    }

    // 便捷配置 API（测试时写死即可）
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

        // 环数组
        sh.setUniformValue1i("u_ringCount", ringCount);
        for (int i = 0; i < ringCount; i++) {
            sh.setUniformValue1f("u_ringR[" + i + "]", ringR[i]);
            sh.setUniformValue1f("u_ringW[" + i + "]", ringW[i]);
            sh.setUniformValue1f("u_ringI[" + i + "]", ringI[i]);
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
        // 法阵大多是发光叠加
        return RenderType.ADDITIVE;
    }
}
