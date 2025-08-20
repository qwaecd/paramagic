package com.qwaecd.paramagic;

import net.minecraftforge.fml.common.Mod;

@Mod(ParaMagic.MOD_ID)
public class ParamagicForge {
    
    public ParamagicForge() {
        ParaMagic.LOG.info("Hello Forge world!");
        CommonClass.init();
        
    }
}