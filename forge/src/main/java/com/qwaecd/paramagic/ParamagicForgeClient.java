package com.qwaecd.paramagic;

import com.qwaecd.paramagic.lifecycle.LifecycleProviderClientForge;
import com.qwaecd.paramagic.lifecycle.ParamagicLifecycleClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Paramagic.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ParamagicForgeClient {
    private ParamagicForgeClient() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ParamagicClient.initOnClient();
        ParamagicLifecycleClient.init(new LifecycleProviderClientForge());
    }
}
