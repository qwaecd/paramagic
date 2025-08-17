package com.qwaecd.paramagic.client.material;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import org.joml.Vector4f;

public class SunMaterial extends AbstractMaterial {
    private final Vector4f sunColor = new Vector4f(0.7f, 0.8f, 0.2f, 1.0f);
    private float intensity = 15.0f;

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
        return RenderType.OPAQUE;
    }
}
