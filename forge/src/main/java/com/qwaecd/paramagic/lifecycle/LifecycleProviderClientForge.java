package com.qwaecd.paramagic.lifecycle;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.lifecycle.api.client.LifecycleProviderClient;
import com.qwaecd.paramagic.lifecycle.event.client.LocalPlayerLeaveWorldHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE;

public final class LifecycleProviderClientForge implements LifecycleProviderClient {
    @Override
    public void registerOnPlayerLeaveWorld(LocalPlayerLeaveWorldHandler handler) {
        ForgeClientLifecycleHooks.registerOnPlayerLeaveWorldHandler(handler);
    }

    @Mod.EventBusSubscriber(modid = Paramagic.MOD_ID, bus = FORGE, value = Dist.CLIENT)
    public static final class ForgeClientLifecycleHooks {
        private static final List<LocalPlayerLeaveWorldHandler> LEAVE_WORLD_HANDLERS = new ArrayList<>();

        private ForgeClientLifecycleHooks() {}

        static void registerOnPlayerLeaveWorldHandler(LocalPlayerLeaveWorldHandler handler) {
            LEAVE_WORLD_HANDLERS.add(handler);
        }

        @SubscribeEvent
        public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
            for (LocalPlayerLeaveWorldHandler handler : LEAVE_WORLD_HANDLERS) {
                handler.onLeave();
            }
        }
    }
}
