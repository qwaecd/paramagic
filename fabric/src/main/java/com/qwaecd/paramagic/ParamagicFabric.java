package com.qwaecd.paramagic;

import com.qwaecd.paramagic.init.ModEntitiesFabric;
import com.qwaecd.paramagic.spell.SpellScheduler;
import com.qwaecd.paramagic.init.ModItemsFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;


public class ParamagicFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Paramagic.LOG.info("Hello Fabric world!");
        CommonClass.init();
        ModItemsFabric.registerAll();
        ModEntitiesFabric.registerAll();

        onServerTickEvent();
    }

    private void onServerTickEvent() {
        ServerTickEvents.END_SERVER_TICK.register(
                server -> {
                    final float deltaTime = 1.0f / 20.0f; // Assuming a fixed tick rate of 20 ticks per second
                    SpellScheduler.getINSTANCE(false).tick(deltaTime);
                }
        );
    }
}
