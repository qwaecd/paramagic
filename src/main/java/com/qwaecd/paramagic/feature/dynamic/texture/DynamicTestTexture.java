package com.qwaecd.paramagic.feature.dynamic.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.qwaecd.paramagic.resource.ModResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;


public class DynamicTestTexture{
    private DynamicTexture magicCircleTexture;
    private ResourceLocation magicCircleRL;
    private boolean active = true;

    public DynamicTestTexture() {
        this.magicCircleTexture = new DynamicTexture(16, 16, true);
        magicCircleRL = ModResource.DEFAULT_MAGIC_CIRCLE;
        Minecraft.getInstance().getTextureManager().register(magicCircleRL, magicCircleTexture);
    }

    public void updateTexture(int x, int y, Color color) {
        NativeImage img = magicCircleTexture.getPixels();
        if (img != null) {
            img.setPixelRGBA(x, y, (color.getAlpha() & 0xFF) << 24 | (color.getRed() & 0xFF) << 16 | (color.getGreen() & 0xFF) << 8 | (color.getBlue() & 0xFF));
        }
        magicCircleTexture.upload();
    }

    public DynamicTexture getMagicCircleTexture() {
        return magicCircleTexture;
    }

    public ResourceLocation getMagicCircleRL() {
        return magicCircleRL;
    }

    public boolean isActive() {
        return active;
    }
}
