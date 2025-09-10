package com.qwaecd.paramagic.core.para.material;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;

public class PolygonMaterial extends AbstractMaterial {
    public PolygonMaterial(Shader shader) {
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
