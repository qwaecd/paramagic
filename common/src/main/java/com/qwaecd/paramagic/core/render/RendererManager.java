package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.client.renderer.MagicCircleRenderer;
import com.qwaecd.paramagic.core.particle.renderer.AdditiveGPUParticleRenderer;
import com.qwaecd.paramagic.core.particle.renderer.ParticleRenderer;
import com.qwaecd.paramagic.core.render.context.RenderContext;
import com.qwaecd.paramagic.core.render.state.GLStateCache;
import com.qwaecd.paramagic.feature.MagicCircleManager;

import java.util.ArrayList;
import java.util.List;

public class RendererManager {
    private final MagicCircleRenderer magicCircleRenderer;
    private final List<ParticleRenderer> particleRenderers;

    public RendererManager() {
        ModRenderSystem rs = ModRenderSystem.getInstance();
        this.magicCircleRenderer = new MagicCircleRenderer(rs);
        this.particleRenderers = new ArrayList<>(1);
        this.particleRenderers.add(new AdditiveGPUParticleRenderer());
    }

    public void submitAll() {
        MagicCircleManager cm = MagicCircleManager.getInstance();
        cm.drawAll(this.magicCircleRenderer);
    }

    public void update(float deltaTime) {
        MagicCircleManager cm = MagicCircleManager.getInstance();
        cm.update(deltaTime);
    }

    public void renderParticles(RenderContext context, GLStateCache stateCache) {
        for (ParticleRenderer renderer : this.particleRenderers) {
            renderer.render(context, stateCache);
        }
    }
}
