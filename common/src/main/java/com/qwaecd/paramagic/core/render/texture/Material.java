package com.qwaecd.paramagic.core.render.texture;

import com.qwaecd.paramagic.core.render.shader.Shader;

public class Material extends AbstractMaterial implements UniformHandler {
    public Material(Shader shader) {
        super(shader);
    }

    @Override
    public void applyCustomUniforms() {
    }
}
