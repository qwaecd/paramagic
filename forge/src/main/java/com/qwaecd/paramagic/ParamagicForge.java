package com.qwaecd.paramagic;

import com.qwaecd.paramagic.init.ModItemsForge;
import com.qwaecd.paramagic.lifecycle.LifecycleProviderForge;
import com.qwaecd.paramagic.lifecycle.ParamagicLifecycle;
import com.qwaecd.paramagic.network.ForgeNetworking;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.platform.ForgeManaAccess;
import com.qwaecd.paramagic.spell.mana.ManaAccess;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Paramagic.MOD_ID)
public class ParamagicForge {
    
    public ParamagicForge() {
        Paramagic.LOG.info("Hello Forge world!");
        ManaAccess.initialize(new ForgeManaAccess());
        ForgeManaAccess.register();
        Networking.init(new ForgeNetworking());
        Paramagic.init();
        ModItemsForge.registerAll(FMLJavaModLoadingContext.get().getModEventBus());
        ParamagicLifecycle.init(new LifecycleProviderForge());
    }
}
