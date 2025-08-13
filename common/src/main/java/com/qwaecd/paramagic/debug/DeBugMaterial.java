package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.queue.SupportsRenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;
import com.qwaecd.paramagic.core.render.texture.AbstractMaterial;
import com.qwaecd.paramagic.core.render.texture.UniformHandler;

public class DeBugMaterial extends AbstractMaterial implements UniformHandler, SupportsRenderType {
    public DeBugMaterial(Shader shader) {
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
