package com.qwaecd.paramagic.platform;

import com.qwaecd.paramagic.core.render.RenderContext;
import com.qwaecd.paramagic.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public void initializeOpenGL() {
        // TODO: Implement OpenGL initialization for Forge
    }


}