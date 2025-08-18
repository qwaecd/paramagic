package com.qwaecd.paramagic.client.material;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import org.joml.Vector4f;

import java.awt.*;

public class SunMaterial extends AbstractMaterial {
    /**
     * 本体 LDR 颜色，决定本体颜色，分量不应大于 1.0f
     */
    private final Vector4f sunColor = new Vector4f(1.0f, 0.5f, 0.3f, 0.9f);
    private float intensity = 1.921f;

    public SunMaterial(Shader shader) {
        super(shader);
    }

    public SunMaterial setSunColor(float r, float g, float b) {
        this.sunColor.set(r, g, b);
        return this;
    }

    public SunMaterial setIntensity(float i) {
        this.intensity = i;
        return this;
    }

    @Override
    public void applyCustomUniforms() {
        this.getShader().setUniformValue4f("u_sunColor", sunColor.x, sunColor.y, sunColor.z, sunColor.w);
        this.getShader().setUniformValue1f("u_intensity", intensity);
    }

    @Override
    public RenderType getRenderType() {
        return RenderType.TRANSPARENT;
    }
}
