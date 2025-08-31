package com.qwaecd.paramagic.data.para.material;

import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;

public abstract class ParaMaterial extends AbstractMaterial {
    public ParaMaterial(Shader shader) {
        super(shader);
    }
}
