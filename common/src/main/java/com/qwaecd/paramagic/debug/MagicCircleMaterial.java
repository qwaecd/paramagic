package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.core.render.texture.Texture2D;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class MagicCircleMaterial extends Material {
    private final Map<Integer, Texture2D> textures = new HashMap<>();

    @Getter
    private float time = 0f;
    @Getter
    private float rotationSpeed = 1.0f;
    @Getter
    private float pulseSpeed = 1.0f, pulseAmp = 0.2f;
    @Getter
    private float sweepSpeed = 0.2f, sweepWidth = 0.06f;
    @Getter
    private float intensity = 1.0f;
    @Getter
    private float edgeAA = 0.0f;
    @Getter
    private float uvScaleX = 1f, uvScaleY = 1f;
    @Getter
    private float uvOffsetX = 0f, uvOffsetY = 0f;

    public MagicCircleMaterial(Shader shader, ResourceLocation maskTex) {
        super(shader);
        this.baseColor.set(0.0f, 1.0f, 1.0f, 1.0f);
        textures.put(0, new Texture2D(maskTex, true));
    }

    public MagicCircleMaterial setTime(float t) {
        this.time = t;
        return this;
    }

    public MagicCircleMaterial setRotationSpeed(float v) {
        this.rotationSpeed = v;
        return this;
    }

    public MagicCircleMaterial setPulse(float speed, float amp) {
        this.pulseSpeed = speed;
        this.pulseAmp = amp;
        return this;
    }

    public MagicCircleMaterial setSweep(float speed, float width) {
        this.sweepSpeed = speed;
        this.sweepWidth = width;
        return this;
    }

    public MagicCircleMaterial setIntensity(float i) {
        this.intensity = i;
        return this;
    }

    public MagicCircleMaterial setEdgeAA(float aa) {
        this.edgeAA = aa;
        return this;
    }

    public MagicCircleMaterial setUV(float sx, float sy, float ox, float oy) {
        this.uvScaleX = sx;
        this.uvScaleY = sy;
        this.uvOffsetX = ox;
        this.uvOffsetY = oy;
        return this;
    }


    @Override
    public void unbind() {
        // 如需：解绑纹理或还原状态
        Shader shader = getShader();
        shader.unbind();
    }

    @Override
    public void applyCustomUniforms() {
        Shader shader = getShader();
        shader.bind();

        // 颜色：用 baseColor 作为内外颜色演示（可分开两个颜色）
        shader.setUniformValue4f("uColorInner", baseColor.x, baseColor.y, baseColor.z, baseColor.w);
        shader.setUniformValue4f("uColorOuter", baseColor.x, baseColor.y, baseColor.z, baseColor.w);

        // 动画/参数
        shader.setUniformValue1f("uTime", time);
        shader.setUniformValue1f("uRotationSpeed", rotationSpeed);
        shader.setUniformValue1f("uPulseSpeed", pulseSpeed);
        shader.setUniformValue1f("uPulseAmp", pulseAmp);
        shader.setUniformValue1f("uSweepSpeed", sweepSpeed);
        shader.setUniformValue1f("uSweepWidth", sweepWidth);
        shader.setUniformValue1f("uIntensity", intensity);
        shader.setUniformValue1f("uEdgeAA", edgeAA);
        shader.setUniformValue2f("uUVScale", uvScaleX, uvScaleY);
        shader.setUniformValue2f("uUVOffset", uvOffsetX, uvOffsetY);

        // 纹理与采样器
        Texture2D tex0 = textures.get(0);
        if (tex0 != null) {
            tex0.bind(0);
            shader.setUniformValue1i("uMaskTex", 0);
            shader.setUniformValue2f("uTexelSize", 1.0f / tex0.getWidth(), 1.0f / tex0.getHeight());
        }
    }
}
