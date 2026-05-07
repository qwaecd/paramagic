package com.qwaecd.paramagic.lifecycle;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.lifecycle.api.LifecycleProvider;
import com.qwaecd.paramagic.lifecycle.event.ServerLevelTickHandler;
import com.qwaecd.paramagic.lifecycle.event.ServerTickHandler;
import com.qwaecd.paramagic.lifecycle.event.ServerStoppingHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

public final class LifecycleProviderForge implements LifecycleProvider {
    @Override
    public void registerServerTick(ServerTickHandler handler) {
        ForgeLifecycleHooks.registerServerTickHandler(handler);
    }

    @Override
    public void registerServerLevelTick(ServerLevelTickHandler handler) {
        ForgeLifecycleHooks.registerServerLevelTickHandler(handler);
    }

    @Override
    public void registerServerStopping(ServerStoppingHandler handler) {
        ForgeLifecycleHooks.registerServerStoppingHandler(handler);
    }

    @Mod.EventBusSubscriber(modid = Paramagic.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeLifecycleHooks {
        private static final List<ServerTickHandler> SERVER_TICK_HANDLERS = new ArrayList<>();
        private static final List<ServerLevelTickHandler> SERVER_LEVEL_TICK_HANDLERS = new ArrayList<>();
        private static final List<ServerStoppingHandler> SERVER_STOPPING_HANDLERS = new ArrayList<>();

        private ForgeLifecycleHooks() {}

        static void registerServerTickHandler(ServerTickHandler handler) {
            SERVER_TICK_HANDLERS.add(handler);
        }

        static void registerServerLevelTickHandler(ServerLevelTickHandler handler) {
            SERVER_LEVEL_TICK_HANDLERS.add(handler);
        }

        static void registerServerStoppingHandler(ServerStoppingHandler handler) {
            SERVER_STOPPING_HANDLERS.add(handler);
        }

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.side.isClient()) {
                return;
            }
            for (ServerTickHandler handler : SERVER_TICK_HANDLERS) {
                handler.tick();
            }
        }

        @SubscribeEvent
        public static void onLevelTick(TickEvent.LevelTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.side.isClient()) {
                return;
            }
            if (!(event.level instanceof ServerLevel serverLevel)) {
                return;
            }
            for (ServerLevelTickHandler handler : SERVER_LEVEL_TICK_HANDLERS) {
                handler.tick(serverLevel);
            }
        }

        @SubscribeEvent
        public static void onServerStopping(ServerStoppingEvent event) {
            for (ServerStoppingHandler handler : SERVER_STOPPING_HANDLERS) {
                handler.onStop();
            }
        }
    }
}
