package com.qwaecd.paramagic;

import com.qwaecd.paramagic.client.render.FabricRenderContext;
import com.qwaecd.paramagic.core.render.buffer.WorldBuffer;
import com.qwaecd.paramagic.core.render.shader.RenderHelper;
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
            FabricRenderContext.setCamera(context.camera());
            FabricRenderContext.setPoseStack(context.matrixStack());
            FabricRenderContext.setProjectionMatrix(context.projectionMatrix());
            WorldBuffer buffer = RenderHelper.startLines();
            RenderHelper.drawLine(buffer, 0, 95, 0, 0, 110, 0, Color.RED);
            RenderHelper.endLines(buffer);

            WorldBuffer buffer2 = RenderHelper.startTri();
            RenderHelper.drawTri(buffer2,
                    1, 100, 0,
                    1, 100, 0,
                    1, 110, 0,
                    Color.GREEN);
            RenderHelper.endTri(buffer2);

            WorldBuffer buffer3 = RenderHelper.startTri();
            RenderHelper.drawTri(buffer2,
                    1, 0, 0,
                    0, 1, 0,
                    0, 0, 1,
                    Color.GREEN);
            RenderHelper.endTri(buffer3);
        });
    }
}
