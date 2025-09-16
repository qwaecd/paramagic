package com.qwaecd.paramagic.core.para.material;

import com.qwaecd.paramagic.core.render.EmissiveMutable;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class ParaMaterial extends AbstractMaterial implements EmissiveMutable {
    protected boolean hasColorAnimation;
    public final Vector4f animationColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    protected final Vector3f emissiveColor = new Vector3f(0.1f, 0.1f, 0.1f);
    protected float emissiveIntensity = 0.0f;
    public ParaMaterial(Shader shader) {
        super(shader);
        this.hasColorAnimation = false;
    }

    @Override
    public void applyCustomUniforms() {
        Shader shader = this.shader;
        if (this.hasColorAnimation) {
            shader.setUniformValue1i("u_hasColorAnimation", 1);
            shader.setUniformValue4f("u_animationColor", this.animationColor.x, this.animationColor.y, this.animationColor.z, this.animationColor.w);
        } else {
            shader.setUniformValue1i("u_hasColorAnimation", 0);
        }
        shader.setUniformValue3f("u_emissiveColor", this.emissiveColor.x, this.emissiveColor.y, this.emissiveColor.z);
        shader.setUniformValue1f("u_emissiveIntensity", this.emissiveIntensity);
    }
    @SuppressWarnings({"LombokSetterMayBeUsed", "RedundantSuppression"})
    public void setHasColorAnimation(boolean b) {
        this.hasColorAnimation = b;
    }

    public boolean hasColorAnimation() {
        return this.hasColorAnimation;
    }

    @Override
    public void setEmissiveColor(float r, float g, float b) {
        this.emissiveColor.set(r, g, b);
    }

    public void setEmissiveColor(Vector3f color) {
        this.setEmissiveColor(color.x , color.y, color.z);
    }

    @Override
    public void setEmissiveIntensity(float intensity) {
        this.emissiveIntensity = intensity;
    }
}
