package com.qwaecd.paramagic;

import com.qwaecd.paramagic.client.render.impl.FabricRenderContext;
import com.qwaecd.paramagic.core.render.context.RenderContextManager;
import com.qwaecd.paramagic.core.render.buffer.WorldBuffer;
import com.qwaecd.paramagic.core.render.RenderHelper;
import com.qwaecd.paramagic.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import java.awt.*;

public class ParamagicFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
        Services.PLATFORM.initializeOpenGL();
        WorldRenderEvents.LAST.register(context -> {
            FabricRenderContext fabricContext = new FabricRenderContext(context);

            RenderContextManager.withContext(fabricContext, () -> {
                WorldBuffer buffer = RenderHelper.startLines();
                RenderHelper.drawLine(buffer, 0, 100, 0, 0, 101, 0, Color.RED);
                RenderHelper.endLines(buffer);

                for (int j = 0; j < 128; j++) {
                    for (int i = 0; i < 128; i++) {
                        WorldBuffer buffer2 = RenderHelper.startTri();
                        RenderHelper.drawTri(buffer2,
                                0 + j, 102, i,
                                1 + j, 101, i,
                                0 + j, 101, i,
                                Color.GREEN);
                        RenderHelper.endTri(buffer2);
                    }
                }
            });
        });
    }
}
