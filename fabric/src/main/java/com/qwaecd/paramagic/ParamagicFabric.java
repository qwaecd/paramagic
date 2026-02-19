package com.qwaecd.paramagic;

import com.qwaecd.paramagic.init.*;
import com.qwaecd.paramagic.network.FabricNetworking;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.particle.server.ServerEffectManager;
import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;


public class ParamagicFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Paramagic.LOG.info("Hello Fabric world!");
        Paramagic.init();
        ServerEffectManager.init(runnable -> ServerTickEvents.END_WORLD_TICK.register(level -> runnable.run()));

        ModItemsFabric.registerAll();
        ModBlocksFabric.registerAll();
        ModEntityTypesFabric.registerAll();
        CreativeTableFabric.registerAll();

        ModBlockEntitiesFabric.registerAll();
        ModMenusFabric.registerAll();

        Networking.init(new FabricNetworking());
        AllParaOperators.registerAll();
    }
}
