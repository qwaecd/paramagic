package com.qwaecd.paramagic.core.para.material;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;

public class CurvyStarMaterial extends ParaMaterial {
    public CurvyStarMaterial(Shader shader) {
        super(shader);
    }

    @Override
    public RenderType getRenderType() {
        return RenderType.ADDITIVE;
    }
}
