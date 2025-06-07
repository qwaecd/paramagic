package com.qwaecd.paramagic.client;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
@Deprecated
public class ClientEventHandler {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientMagicCircleManager.tick();
            ClientSpellScheduler.tick();
        }
    }
}
