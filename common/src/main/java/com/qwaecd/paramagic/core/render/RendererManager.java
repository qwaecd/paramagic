package com.qwaecd.paramagic.core.render;

import com.qwaecd.paramagic.client.renderer.MagicCircleRenderer;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;


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

    /**
     * Call this function only within the rendering loop, not in the game logic loop.<br>
     * 仅在渲染循环内调用该函数，不要在游戏逻辑循环内调用。<br>
     * 更新粒子效果的状态，包括其所有发射器的状态。<br>
     * @param deltaTime Seconds of time increment (time since last frame).<br>
     * 时间增量，单位秒（为距离上一帧的时间）<br>
     */
    public void update(float deltaTime) {
        MagicCircleManager cm = MagicCircleManager.getInstance();
        cm.update(deltaTime);
    }
}
