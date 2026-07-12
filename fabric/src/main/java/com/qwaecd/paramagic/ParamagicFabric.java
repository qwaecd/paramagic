package com.qwaecd.paramagic;

import com.qwaecd.paramagic.init.*;
import com.qwaecd.paramagic.lifecycle.FabricManaSyncEvents;
import com.qwaecd.paramagic.lifecycle.LifecycleProviderFabric;
import com.qwaecd.paramagic.lifecycle.ParamagicLifecycle;
import com.qwaecd.paramagic.network.FabricNetworking;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.platform.FabricManaAccess;
import com.qwaecd.paramagic.spell.caster.ManaAccess;
import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import net.fabricmc.api.ModInitializer;


public class ParamagicFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Paramagic.LOG.info("Hello Fabric world!");
        ManaAccess.initialize(new FabricManaAccess());
        Paramagic.init();

        ModItemsFabric.registerAll();
        ModBlocksFabric.registerAll();
        ModEntityTypesFabric.registerAll();
        CreativeTableFabric.registerAll();
        ModSoundsFabric.registerAll();

        ModBlockEntitiesFabric.registerAll();
        ModMenusFabric.registerAll();

        Networking.init(new FabricNetworking());
        FabricManaSyncEvents.register();
        AllParaOperators.registerAll();
        ParamagicLifecycle.init(new LifecycleProviderFabric());
    }
}
