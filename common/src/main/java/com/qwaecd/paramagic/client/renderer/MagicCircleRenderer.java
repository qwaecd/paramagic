package com.qwaecd.paramagic.client.renderer;

import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.ModRenderSystem;

public class MagicCircleRenderer {
    private final ModRenderSystem renderSystem;
    public MagicCircleRenderer(ModRenderSystem renderSystem) {
        this.renderSystem = renderSystem;
    }
    public void submit(IRenderable renderable) {
        renderSystem.addRenderable(renderable);
    }
}
