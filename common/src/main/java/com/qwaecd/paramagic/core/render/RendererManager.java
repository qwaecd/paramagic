package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.client.renderer.MagicCircleRenderer;
import com.qwaecd.paramagic.feature.MagicCircleManager;


public class RendererManager {
    private final MagicCircleRenderer magicCircleRenderer;

    public RendererManager() {
        ModRenderSystem rs = ModRenderSystem.getInstance();
        this.magicCircleRenderer = new MagicCircleRenderer(rs);
    }

    public void submitAll() {
        MagicCircleManager cm = MagicCircleManager.getInstance();
        cm.drawAll(this.magicCircleRenderer);
    }

    public void update(float deltaTime) {
        MagicCircleManager cm = MagicCircleManager.getInstance();
        cm.update(deltaTime);
    }
}
