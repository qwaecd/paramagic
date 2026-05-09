package com.qwaecd.paramagic;

import com.qwaecd.paramagic.init.ModItemsForge;
import com.qwaecd.paramagic.lifecycle.LifecycleProviderForge;
import com.qwaecd.paramagic.lifecycle.ParamagicLifecycle;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Paramagic.MOD_ID)
public class ParamagicForge {
    
    public ParamagicForge() {
        Paramagic.LOG.info("Hello Forge world!");
        Paramagic.init();
        ModItemsForge.registerAll(FMLJavaModLoadingContext.get().getModEventBus());
        ParamagicLifecycle.init(new LifecycleProviderForge());
    }
}
