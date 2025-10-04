package com.qwaecd.paramagic.client.renderer;

import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.api.IRenderable;

public class MagicCircleRenderer {
    private final ModRenderSystem renderSystem;
    public MagicCircleRenderer(ModRenderSystem renderSystem) {
        this.renderSystem = renderSystem;
    }
    public void submit(IRenderable renderable) {
        renderSystem.addRenderable(renderable);
    }
}
