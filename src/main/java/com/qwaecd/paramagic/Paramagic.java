package com.qwaecd.paramagic;

import com.qwaecd.paramagic.capability.ManaCapability;
import com.qwaecd.paramagic.client.ClientEventHandler;
import com.qwaecd.paramagic.client.renderer.MagicCircleRenderer;
import com.qwaecd.paramagic.config.Config;
import com.qwaecd.paramagic.init.MagicMapRegistry;
import com.qwaecd.paramagic.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.qwaecd.paramagic.init.ModItems.ITEMS;
import static com.qwaecd.paramagic.init.ModItems.TABS;

@Mod(Paramagic.MODID)
public class Paramagic
{
    public static final String MODID = "paramagic";
    public Paramagic(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        ITEMS.register(modEventBus);
        TABS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().register(ManaCapability.class);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        });
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
//            ManaCapability.register();
            NetworkHandler.init();
            MagicMapRegistry.init();
        });
    }
    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
//            MagicCircleRenderer.init();
        });
    }
}
