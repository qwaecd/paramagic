package com.qwaecd.paramagic.resource;

import net.minecraft.resources.ResourceLocation;

import static com.qwaecd.paramagic.Paramagic.MODID;

public class ModResource {
    public static final ResourceLocation TEST_RESOURCE;
    public static final ResourceLocation GUIDE_BOOK_SCREEN;
    public static final ResourceLocation TEST_MAGIC_CIRCLE;
    public static final ResourceLocation DEFAULT_MAGIC_CIRCLE;

    static {
        TEST_RESOURCE = setResource("textures/gui/test_resource.png");
        GUIDE_BOOK_SCREEN = setResource("textures/gui/star.png");
        TEST_MAGIC_CIRCLE = setResource("textures/magic_circle/fire.png");
        DEFAULT_MAGIC_CIRCLE = setResource("textures/magic_circle/default.png");
    }

    private static ResourceLocation setResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
