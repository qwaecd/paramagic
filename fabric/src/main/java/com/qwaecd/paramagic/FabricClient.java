package com.qwaecd.paramagic;

import com.qwaecd.paramagic.client.render.impl.FabricRenderContext;
import com.qwaecd.paramagic.client.renderer.entity.SpellAnchorEntityRenderer;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.context.RenderContextManager;
import com.qwaecd.paramagic.debug.DebugTools;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.init.ModEntitiesFabric;
import com.qwaecd.paramagic.network.ClientNetworking;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.platform.Services;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.network.chat.Component;

public class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParamagicClient.initOnClient();

        Services.PLATFORM.initializeOpenGL();
        registerClientCommands();
        registerEntityRenderers();

        ClientNetworking.registerAllOnClient(Networking.get());

        WorldRenderEvents.LAST.register(context -> {
            FabricRenderContext fabricContext = new FabricRenderContext(context);
            RenderContextManager.setContext(fabricContext);
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> SessionManagers.getForClient().tickAll(mc.level, 1.0f / 20.0f));
    }
    private static void registerEntityRenderers() {
        EntityRendererRegistry.register(ModEntitiesFabric.SPELL_ANCHOR_ENTITY, SpellAnchorEntityRenderer::new);
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
                                        MagicCircleManager.getInstance().removeAllCircles();
                                        DebugTools.clearTestEffects();
                                        context.getSource().sendFeedback(Component.literal("cleared!"));
                                        return 1;
                                    })
                    );
                }
        );
    }
}
