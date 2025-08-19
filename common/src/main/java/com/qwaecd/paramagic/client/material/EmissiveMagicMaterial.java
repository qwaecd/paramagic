package com.qwaecd.paramagic.client.material;

import com.qwaecd.paramagic.core.render.EmissiveProvider;
import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import lombok.Setter;
import org.joml.Vector3f;

public class EmissiveMagicMaterial extends MagicCircleMaterial implements EmissiveProvider {
    private final Vector3f emissiveColor = new Vector3f(0.2f, 0.3f, 0.5f);
    @Setter
    private float emissiveIntensity = 5.0f;

    public EmissiveMagicMaterial(Shader shader) {
        super(shader);
        this.setBaseColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void applyCustomUniforms() {
        super.applyCustomUniforms();
        this.getShader().setUniformValue3f(EMISSIVE_COLOR_UNIFORM, emissiveColor.x, emissiveColor.y, emissiveColor.z);
        this.getShader().setUniformValue1f(EMISSIVE_INTENSITY_UNIFORM, emissiveIntensity);
    }

    public void setEmissiveColor(float r, float g, float b) {
        this.emissiveColor.set(r, g, b);
    }

    @Override
    public Vector3f getEmissiveColor() {
        return this.emissiveColor;
    }

    @Override
    public float getEmissiveIntensity() {
        return this.emissiveIntensity;
    }
    @Override
    public RenderType getRenderType() {
        return RenderType.TRANSPARENT;
    }
}
