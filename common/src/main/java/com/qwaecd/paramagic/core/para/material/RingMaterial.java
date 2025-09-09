package com.qwaecd.paramagic.core.para.material;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;

public class RingMaterial extends AbstractMaterial {
    public RingMaterial(Shader shader) {
        super(shader);
    }

    @Override
    public void applyCustomUniforms() {

    }

    @Override
    public RenderType getRenderType() {
        return RenderType.ADDITIVE;
    }
}
