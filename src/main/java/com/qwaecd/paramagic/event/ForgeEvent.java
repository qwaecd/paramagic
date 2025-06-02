package com.qwaecd.paramagic.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.qwaecd.paramagic.Paramagic.MODID;
import static com.qwaecd.paramagic.feature.SpellExecutor.tick;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvent {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tick();
        }
    }

//    @SubscribeEvent
//    public static void onClientTick(TickEvent.ClientTickEvent event){
//        if (event.phase == TickEvent.Phase.END) {
//            tick();
//        }
//    }
}
