package com.qwaecd.paramagic;

import com.qwaecd.paramagic.client.render.impl.FabricCamera;
import com.qwaecd.paramagic.client.render.impl.FabricPoseStack;
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
            FabricRenderContext fabricContext = new FabricRenderContext(
                    new FabricCamera(context.camera()),
                    new FabricPoseStack(context.matrixStack()),
                    context.projectionMatrix()
            );

            RenderContextManager.withContext(fabricContext, () -> {
                WorldBuffer buffer = RenderHelper.startLines();
                RenderHelper.drawLine(buffer, 0, 100, 0, 0, 101, 0, Color.RED);
                RenderHelper.endLines(buffer);

                WorldBuffer buffer2 = RenderHelper.startTri();
                RenderHelper.drawTri(buffer2,
                        0, 102, 0,
                        1, 101, 0,
                        0, 101, 0,
                        Color.GREEN);
                RenderHelper.endTri(buffer2);
            });
        });
    }
}
