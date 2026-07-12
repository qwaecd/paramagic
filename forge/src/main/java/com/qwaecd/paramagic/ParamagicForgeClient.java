package com.qwaecd.paramagic;

import com.qwaecd.paramagic.client.input.ForgeKeyBindings;
import com.qwaecd.paramagic.lifecycle.LifecycleProviderClientForge;
import com.qwaecd.paramagic.lifecycle.ParamagicLifecycleClient;
import com.qwaecd.paramagic.network.ClientNetworking;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.spell.mana.ClientManaState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
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
        ClientNetworking.registerAllOnClient(Networking.get());
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ForgeKeyBindings.registerAll(event);
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(
                VanillaGuiOverlay.HOTBAR.id(),
                "mana",
                (gui, guiGraphics, partialTick, screenWidth, screenHeight) ->
                        ClientManaState.displayHud(guiGraphics, partialTick)
        );
    }
}
