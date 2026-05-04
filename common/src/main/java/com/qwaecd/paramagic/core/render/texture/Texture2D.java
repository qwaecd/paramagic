package com.qwaecd.paramagic.core.render.texture;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import static org.lwjgl.opengl.GL33.GL_CLAMP_TO_EDGE;

@Getter
public final class Texture2D {
    private final ResourceLocation location;
    private final boolean generateMipmap;
    private final int wrapS;
    private final int wrapT;
    private int width = -1;
    private int height = -1;

    public Texture2D(ResourceLocation location, boolean generateMipmap) {
        this(location, generateMipmap, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE);
    }

    public Texture2D(ResourceLocation location, boolean generateMipmap, int wrapS, int wrapT) {
        this.location = location;
        this.generateMipmap = generateMipmap;
        this.wrapS = wrapS;
        this.wrapT = wrapT;
    }

    void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
