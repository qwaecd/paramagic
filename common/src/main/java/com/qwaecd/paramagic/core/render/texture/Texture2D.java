package com.qwaecd.paramagic.core.render.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.qwaecd.paramagic.ParaMagic;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL33.*;

@Getter
public class Texture2D {
    private final int id;
    private int width, height;

    public Texture2D(ResourceLocation location, boolean generateMipmap) {
        this.id = glGenTextures();
        bind(0);
        try (InputStream in = Minecraft.getInstance().getResourceManager().getResource(location).get().open();
            NativeImage img = NativeImage.read(in)) {
            this.width = img.getWidth();
            this.height = img.getHeight();

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, generateMipmap ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, img.getPixelsRGBA());

            if (generateMipmap) {
                glGenerateMipmap(GL_TEXTURE_2D);
            }
        } catch (IOException e) {
            ParaMagic.LOG.error("Failed to load texture: {}", location, e);
        }
        unbind();
    }

    public void bind(int unit) {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void dispose() {
        glDeleteTextures(id);
    }

}