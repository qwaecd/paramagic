package com.qwaecd.paramagic;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ParaMagic.MODID)
public class ParaMagic
{
    public static final String MODID = "paramagic";
    public ParaMagic(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
//        modEventBus.addListener(this::commonSetup);
//        MinecraftForge.EVENT_BUS.register(this);
    }
}
