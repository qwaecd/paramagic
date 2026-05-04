package com.qwaecd.paramagic.core.render.api;

import com.qwaecd.paramagic.core.render.ModRenderSystem;

public interface RenderEffect extends AutoCloseable {
    void onAdded(ModRenderSystem renderSystem);
    void update(float deltaTime);
    boolean isAlive();
    void close();
}
