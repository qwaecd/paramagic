package com.qwaecd.paramagic;

import com.qwaecd.paramagic.client.render.impl.FabricRenderContext;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.context.RenderContextManager;
import com.qwaecd.paramagic.debug.DebugTools;
import com.qwaecd.paramagic.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.network.chat.Component;


public class ParamagicFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ParaMagic.LOG.info("Hello Fabric world!");
        CommonClass.init();
        Services.PLATFORM.initializeOpenGL();
        registerClientCommands();

        WorldRenderEvents.LAST.register(context -> {
            FabricRenderContext fabricContext = new FabricRenderContext(context);
            RenderContextManager.setContext(fabricContext);
        });
    }

    private static void registerClientCommands() {
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) -> {
                    dispatcher.register(
                            ClientCommandManager.literal("para_show")
                                    .executes(context -> {
                                        DebugTools.test();
                                        context.getSource().sendFeedback(Component.literal("show!"));
                                        return 1;
                                    })
                    );
                    dispatcher.register(
                            ClientCommandManager.literal("para_remove")
                                    .executes(context -> {
                                        ModRenderSystem.getInstance().clearAll();
                                        context.getSource().sendFeedback(Component.literal("cleared!"));
                                        return 1;
                                    })
                    );
                }
        );
    }
}
