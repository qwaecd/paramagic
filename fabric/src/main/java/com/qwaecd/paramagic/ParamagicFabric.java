package com.qwaecd.paramagic;

import com.qwaecd.paramagic.entity.ModEntityTypes;
import com.qwaecd.paramagic.init.ModEntitiesFabric;
import com.qwaecd.paramagic.init.ModItemsFabric;
import net.fabricmc.api.ModInitializer;


public class ParamagicFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Paramagic.LOG.info("Hello Fabric world!");
        CommonClass.init();
        ModItemsFabric.registerAll();
        ModEntitiesFabric.registerAll();

        ModEntityTypes.init(ModEntitiesFabric.instance);
    }
}
