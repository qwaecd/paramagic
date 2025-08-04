package com.qwaecd.paramagic;

import com.qwaecd.paramagic.core.render.buffer.WorldBuffer;
import com.qwaecd.paramagic.core.render.shader.RenderHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import java.awt.*;

public class ParamagicFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
        WorldRenderEvents.LAST.register(context -> {
            WorldBuffer buffer = RenderHelper.startLines();
            RenderHelper.drawLine(buffer, 0, 100, 0, 0, 105, 0, Color.RED);
            RenderHelper.endLines(buffer);
        });
    }
}
