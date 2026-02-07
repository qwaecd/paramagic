package com.qwaecd.paramagic.ui.util;

import net.minecraft.resources.ResourceLocation;

public class Sprite {
    public static final int DEFAULT_TEXTURE_SIZE = 64;
    public final ResourceLocation texture;
    // 该精灵图位于纹理中的 uv 坐标
    public final int u, v;
    // 该精灵图的尺寸
    public final int width, height;
    // 纹理的总尺寸
    public final int texWidth, texHeight;

    public Sprite(ResourceLocation texture, int u, int v, int width, int height, int texWidth, int texHeight) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    public static Sprite of(ResourceLocation texture, int u, int v, int w, int h) {
        return new Sprite(texture, u, v, w, h, DEFAULT_TEXTURE_SIZE, DEFAULT_TEXTURE_SIZE);
    }
}
