package com.qwaecd.paramagic;

import com.qwaecd.paramagic.init.CreativeTableFabric;
import com.qwaecd.paramagic.init.ModBlocksFabric;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import com.qwaecd.paramagic.init.ModEntitiesFabric;
import com.qwaecd.paramagic.init.ModItemsFabric;
import com.qwaecd.paramagic.network.FabricNetworking;
import com.qwaecd.paramagic.network.Networking;
import net.fabricmc.api.ModInitializer;


public class ParamagicFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Paramagic.LOG.info("Hello Fabric world!");
        Paramagic.init();
        ModBlocksFabric.registerAll();
        ModItemsFabric.registerAll();
        ModEntitiesFabric.registerAll();
        CreativeTableFabric.registerAll();

        ModEntityTypes.init(ModEntitiesFabric.instance);

        Networking.init(new FabricNetworking());
    }
}
