package com.qwaecd.paramagic.client.renderer;

import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.feature.MagicCircleManager;

public class RendererManager {
    private final MagicCircleRenderer magicCircleRenderer;

    public RendererManager() {
        ModRenderSystem rs = ModRenderSystem.getInstance();
        this.magicCircleRenderer = new MagicCircleRenderer(rs);
    }

    public void submitAll() {
        MagicCircleManager.getInstance().drawAll(this.magicCircleRenderer);
    }
}
