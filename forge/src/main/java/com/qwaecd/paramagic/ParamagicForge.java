package com.qwaecd.paramagic;

import com.qwaecd.paramagic.init.ModItemsForge;
import com.qwaecd.paramagic.lifecycle.LifecycleProviderForge;
import com.qwaecd.paramagic.lifecycle.ParamagicLifecycle;
import com.qwaecd.paramagic.network.ForgeNetworking;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.platform.ForgeManaAccess;
import com.qwaecd.paramagic.spell.mana.ManaAccess;
import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Paramagic.MOD_ID)
public class ParamagicForge {
    
    public ParamagicForge() {
        Paramagic.LOG.info("Hello Forge world!");
        ManaAccess.initialize(new ForgeManaAccess());
        ForgeManaAccess.register();
        Networking.init(new ForgeNetworking());
        // 必须早于 Paramagic.init() 内的 Lang.init() 与后续 ModItemsForge.registerAll()，
        // 以保证 OperatorRegistration 的 REGISTRY 已被填充，从而驱动 lang/item/operator 三处注册。
        AllParaOperators.registerAll();
        Paramagic.init();
        ModItemsForge.registerAll(FMLJavaModLoadingContext.get().getModEventBus());
        ParamagicLifecycle.init(new LifecycleProviderForge());
    }
}
