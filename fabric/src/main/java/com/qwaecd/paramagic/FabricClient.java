package com.qwaecd.paramagic;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.qwaecd.paramagic.client.render.impl.FabricRenderContext;
import com.qwaecd.paramagic.client.replay.FabricReplayCompat;
import com.qwaecd.paramagic.client.renderer.entity.ModEntityRenderers;
import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.context.RenderContextManager;
import com.qwaecd.paramagic.debug.DebugTools;
import com.qwaecd.paramagic.debug.ParamagicDebugState;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.init.ModBlockEntityRendererFabric;
import com.qwaecd.paramagic.lifecycle.LifecycleProviderClientFabric;
import com.qwaecd.paramagic.lifecycle.ParamagicLifecycleClient;
import com.qwaecd.paramagic.network.ClientNetworking;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRegistry;
import com.qwaecd.paramagic.platform.Services;
import com.qwaecd.paramagic.spell.core.SessionManagers;
import com.qwaecd.paramagic.ui.screen.ModScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class FabricClient implements ClientModInitializer {
    private static final String DEBUG_PARTICLE_STATS_TITLE = "debug.paramagic.particle_stats.title";
    private static final String DEBUG_PARTICLE_STATS_ALIVE = "debug.paramagic.particle_stats.alive";
    private static final String DEBUG_PARTICLE_STATS_FREE = "debug.paramagic.particle_stats.free";
    private static final String DEBUG_PARTICLE_STATS_SUCCESSFUL_EMISSION_TASKS = "debug.paramagic.particle_stats.successful_emission_tasks";
    private static final String DEBUG_ACTIVE_GPU_EFFECT_COUNT = "debug.paramagic.active_gpu_effect_count";

    @Override
    public void onInitializeClient() {
        ParamagicClient.initOnClient();
        ParamagicLifecycleClient.init(new LifecycleProviderClientFabric());
        FabricReplayCompat.init();

        Services.PLATFORM.initializeOpenGL();
        registerClientCommands();
        registerEntityRenderers();

        ClientNetworking.registerAllOnClient(Networking.get());

        WorldRenderEvents.LAST.register(context -> {
            FabricRenderContext fabricContext = new FabricRenderContext(context);
            RenderContextManager.setContext(fabricContext);
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> SessionManagers.getForClient().tickAll(mc.level));
        registerScreens();
        registerDebugTools();
    }

    private static void registerDebugTools() {
        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            if (!ParamagicDebugState.showParticleInfo()) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.options.renderDebug) {
                return;
            }
            Font font = minecraft.font;
            ParticleSystem ps = ParticleSystem.getInstance();
            final int x = 4;
            int y = 4;
            final int lineHeight = font.lineHeight + 2;
            final int color = 0xE0E0E0;
            guiGraphics.drawString(font, Component.translatable(DEBUG_PARTICLE_STATS_TITLE), x, y, color, true);
            y += lineHeight;
            guiGraphics.drawString(font, Component.translatable(DEBUG_PARTICLE_STATS_ALIVE, ps.getDebugAliveParticleCount()), x, y, color, true);
            y += lineHeight;
            guiGraphics.drawString(font, Component.translatable(DEBUG_PARTICLE_STATS_FREE, ps.getDebugFreeParticleCount()), x, y, color, true);
            y += lineHeight;
            guiGraphics.drawString(font, Component.translatable(DEBUG_ACTIVE_GPU_EFFECT_COUNT, ps.getActiveEffectCount(), ps.MAX_EFFECT_COUNT), x, y, color, true);
            y += lineHeight;
            guiGraphics.drawString(font, Component.translatable(DEBUG_PARTICLE_STATS_SUCCESSFUL_EMISSION_TASKS, ps.getDebugSuccessfulTaskCount()), x, y, color, true);
        });
    }

    private static void registerScreens() {
        ModScreens.RegistryProvider provider = new ModScreens.RegistryProvider() {
            @Override
            public <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>>
            void register(MenuType<M> menuType, ModScreens.ScreenFactory<M, S> factory) {
                MenuScreens.register(menuType, factory::create);
            }
        };
        ModScreens.init(provider);
    }

    private static void registerEntityRenderers() {
        var provider = new ModEntityRenderers.RendererProvider() {
            @Override
            public <T extends Entity> void register(EntityType<T> type, ModEntityRenderers.RendererFactory<T> factory) {
                EntityRendererRegistry.register(type, factory::get);
            }
        };
        ModEntityRenderers.init(provider);
        ModBlockEntityRendererFabric.registerAll();
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
                                        SharedGPUEffectRegistry.reset();
                                        context.getSource().sendFeedback(Component.literal("cleared!"));
                                        return 1;
                                    })
                    );
                    dispatcher.register(ClientCommandManager.literal("paramagic")
                            .then(ClientCommandManager.literal("debug")
                                    .then(ClientCommandManager.literal("showParticleInfo")
                                            .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                                        ParamagicDebugState.setShowParticleInfo(enabled);
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                    );
                }
        );
    }
}
