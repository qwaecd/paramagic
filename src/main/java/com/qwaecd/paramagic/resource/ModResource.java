package com.qwaecd.paramagic.resource;

import net.minecraft.resources.ResourceLocation;

import static com.qwaecd.paramagic.Paramagic.MODID;

public class ModResource {
    public static final ResourceLocation TEST_RESOURCE;
    public static final ResourceLocation GUIDE_BOOK_SCREEN;

    static {
        TEST_RESOURCE = setResource("textures/gui/test_resource.png");
        GUIDE_BOOK_SCREEN = setResource("textures/gui/star.png");
    }

    private static ResourceLocation setResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
