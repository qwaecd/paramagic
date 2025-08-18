package com.qwaecd.paramagic.core.render.texture;

import com.qwaecd.paramagic.core.render.queue.RenderType;
import com.qwaecd.paramagic.core.render.queue.SupportsRenderType;
import com.qwaecd.paramagic.core.render.shader.Shader;

public class Material extends AbstractMaterial implements SupportsRenderType {
    public Material(Shader shader) {
        super(shader);
    }

    @Override
    public void applyCustomUniforms() {
    }

    @Override
    public RenderType getRenderType() {
        return RenderType.OPAQUE;
    }
}
