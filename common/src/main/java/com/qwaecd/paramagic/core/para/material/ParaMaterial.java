package com.qwaecd.paramagic.core.para.material;

import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import org.joml.Vector4f;

public abstract class ParaMaterial extends AbstractMaterial {
    protected boolean hasColorAnimation;
    public final Vector4f animationColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public ParaMaterial(Shader shader) {
        super(shader);
        this.hasColorAnimation = false;
    }

    @Override
    public void applyCustomUniforms() {
        Shader shader = this.getShader();
        if (this.hasColorAnimation) {
            shader.setUniformValue1i("u_hasColorAnimation", 1);
            shader.setUniformValue4f("u_animationColor", this.animationColor.x, this.animationColor.y, this.animationColor.z, this.animationColor.w);
        } else {
            shader.setUniformValue1i("u_hasColorAnimation", 0);
        }
    }
    @SuppressWarnings({"LombokSetterMayBeUsed", "RedundantSuppression"})
    public void setHasColorAnimation(boolean b) {
        this.hasColorAnimation = b;
    }

    public boolean hasColorAnimation() {
        return this.hasColorAnimation;
    }
}
