package com.qwaecd.paramagic.client.material;

import com.qwaecd.paramagic.core.render.EmissiveProvider;
import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;

public class SunMaterial extends AbstractMaterial implements EmissiveProvider {
    /**
     * 本体 LDR 颜色，决定本体颜色，分量不应大于 1.0f
     */
    private final Vector4f sunColor;
    private final Vector3f emissiveColor;
    private float intensity = 2.921f;

    public SunMaterial(Shader shader) {
        super(shader);
        this.sunColor = new Vector4f(1.0f, 0.6f, 0.3f, 0.7f);
        this.emissiveColor = new Vector3f(sunColor.x, sunColor.y, sunColor.z);
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

    @Override
    public Vector3f getEmissiveColor() {
        return this.emissiveColor;
    }

    @Override
    public float getEmissiveIntensity() {
        return this.intensity;
    }
}
