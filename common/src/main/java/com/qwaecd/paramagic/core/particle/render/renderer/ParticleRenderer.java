package com.qwaecd.paramagic.core.particle.render.renderer;

import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.state.GLStateCache;

public abstract class ParticleRenderer {

    public abstract void render(RenderContext context, GLStateCache stateCache);
    public abstract ParticleRendererType getType();
}
