package com.qwaecd.paramagic;

import com.qwaecd.paramagic.client.render.impl.FabricRenderContext;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.context.RenderContextManager;
import com.qwaecd.paramagic.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.network.chat.Component;


public class ParamagicFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
        Services.PLATFORM.initializeOpenGL();
        registerClientCommands();

        WorldRenderEvents.LAST.register(context -> {
            FabricRenderContext fabricContext = new FabricRenderContext(context);
            ModRenderSystem.getInstance().renderScene(fabricContext);

            RenderContextManager.withContext(fabricContext, () -> {

//                WorldBuffer buffer = RenderHelper.startLines();
//                RenderHelper.drawLine(buffer, 0, 100, 0, 0, 101, 0, Color.RED);
//                RenderHelper.endLines(buffer);
//
//                WorldBuffer buffer1 = RenderHelper.startLines();
//                RenderHelper.drawLine(buffer1, 0, 2, 0, 0, 0, 0, Color.GREEN);
//                RenderHelper.endLines(buffer1);

                /*for (int j = 0; j < 128; j++) {
                    for (int i = 0; i < 128; i++) {
                        WorldBuffer buffer2 = RenderHelper.startTri();
                        RenderHelper.drawTri(buffer2,
                                0 + j, 102, i,
                                1 + j, 101, i,
                                0 + j, 101, i,
                                Color.GREEN);
                        RenderHelper.endTri(buffer2);
                    }
                }*/
            });
        });
    }

    private static void registerClientCommands() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> {
                    dispatcher.register(
                            ClientCommandManager.literal("para_show")
                                    .executes(context -> {
                                        ModRenderSystem.getInstance().test();
                                        context.getSource().sendFeedback(Component.literal("show!"));
                                        return 1;
                                    })
                    );
                    dispatcher.register(
                            ClientCommandManager.literal("para_remove")
                                    .executes(context -> {
                                        ModRenderSystem.getInstance().clearAllScene();
                                        context.getSource().sendFeedback(Component.literal("cleared!"));
                                        return 1;
                                    })
                    );
                }
        );
    }
}
