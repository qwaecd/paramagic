package com.qwaecd.paramagic;

import com.qwaecd.paramagic.init.*;
import com.qwaecd.paramagic.network.FabricNetworking;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import net.fabricmc.api.ModInitializer;


public class ParamagicFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Paramagic.LOG.info("Hello Fabric world!");
        Paramagic.init();
        ModItemsFabric.registerAll();
        ModBlocksFabric.registerAll();
        ModEntitiesFabric.registerAll();
        CreativeTableFabric.registerAll();

        ModEntityTypes.init(ModEntitiesFabric.instance);
        ModMenusFabric.registerAll();

        Networking.init(new FabricNetworking());
    }
}
