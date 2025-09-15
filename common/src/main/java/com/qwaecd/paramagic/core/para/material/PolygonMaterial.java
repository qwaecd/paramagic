package com.qwaecd.paramagic.core.para.material;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;

public class PolygonMaterial extends ParaMaterial {
    public PolygonMaterial(Shader shader) {
        super(shader);
    }

    @Override
    public void applyCustomUniforms() {
        super.applyCustomUniforms();
    }

    @Override
    public RenderType getRenderType() {
        return RenderType.ADDITIVE;
    }
}
