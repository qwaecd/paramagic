package com.qwaecd.paramagic;

import com.qwaecd.paramagic.init.MagicMapRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        MagicMapRegistry.init();
    }
}
