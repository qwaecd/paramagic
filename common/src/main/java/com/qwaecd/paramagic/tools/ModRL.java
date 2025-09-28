package com.qwaecd.paramagic.tools;

import com.qwaecd.paramagic.Paramagic;
import net.minecraft.resources.ResourceLocation;

public final class ModRL {
    public static ResourceLocation of(String namespace, String location) {
        return new ResourceLocation(namespace, location);
    }

    public static ResourceLocation InModSpace(String location) {
        return new ResourceLocation(Paramagic.MOD_ID, location);
    }
}
