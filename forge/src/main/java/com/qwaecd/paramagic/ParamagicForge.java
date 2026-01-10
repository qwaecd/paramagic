package com.qwaecd.paramagic;

import net.minecraftforge.fml.common.Mod;

@Mod(Paramagic.MOD_ID)
public class ParamagicForge {
    
    public ParamagicForge() {
        Paramagic.LOG.info("Hello Forge world!");
        Paramagic.init();
    }
}