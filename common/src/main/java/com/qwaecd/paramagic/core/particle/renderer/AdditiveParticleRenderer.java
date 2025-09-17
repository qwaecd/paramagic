package com.qwaecd.paramagic.core.particle.renderer;

import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.state.GLStateCache;
import com.qwaecd.paramagic.core.render.state.RenderState;

public class AdditiveParticleRenderer extends ParticleRenderer {
    @Override
    public void render(RenderContext context, GLStateCache stateCache) {
        stateCache.apply(RenderState.ADDITIVE);
    }

    @Override
    public ParticleRendererType getType() {
        return ParticleRendererType.ADDITIVE;
    }

    @Override
    public void close() throws Exception {

    }
}
