package com.qwaecd.paramagic;

import com.qwaecd.paramagic.init.ModItems;
import net.fabricmc.api.ModInitializer;


public class ParamagicFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Paramagic.LOG.info("Hello Fabric world!");
        CommonClass.init();
        ModItems.registerAll();
    }
}
