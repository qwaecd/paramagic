package com.qwaecd.paramagic.core.render.texture;

import com.qwaecd.paramagic.core.render.shader.Shader;

public class BaseMaterial extends Material implements UniformHandler{
    public BaseMaterial(Shader shader) {
        super(shader);
    }

    @Override
    public void applyCustomUniforms() {

    }
}
