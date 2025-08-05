package com.qwaecd.paramagic.platform;

import com.qwaecd.paramagic.client.render.FabricRenderContext;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.RenderContext;
import com.qwaecd.paramagic.core.render.buffer.BufferManager;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void initializeOpenGL() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            ModRenderSystem.initAfterClientStarted();
        });
    }

    @Override
    public RenderContext getRenderContext() {
        return FabricRenderContext.INSTANCE;
    }
}
