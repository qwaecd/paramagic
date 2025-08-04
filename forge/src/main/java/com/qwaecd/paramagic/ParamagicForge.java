package com.qwaecd.paramagic;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ParamagicForge {
    
    public ParamagicForge() {
        Constants.LOG.info("Hello Forge world!");
        CommonClass.init();
        
    }
}