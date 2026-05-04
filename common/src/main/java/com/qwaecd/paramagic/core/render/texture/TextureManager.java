package com.qwaecd.paramagic.core.render.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.qwaecd.paramagic.Paramagic;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.opengl.GL33.*;

public final class TextureManager implements AutoCloseable {
    private final Map<TextureKey, ManagedTexture> cache = new HashMap<>();
    private final Set<TextureKey> failedTextures = new HashSet<>();

    public int getTextureId(Texture2D texture) {
        if (texture == null) {
            return TextureUtils.getBlackTexture();
        }
        TextureKey key = TextureKey.from(texture);
        if (failedTextures.contains(key)) {
            return TextureUtils.getBlackTexture();
        }
        ManagedTexture cached = cache.get(key);
        if (cached != null) {
            texture.setDimensions(cached.width, cached.height);
            return cached.id;
        }
        ManagedTexture loaded = loadTexture(key);
        if (loaded == null) {
            failedTextures.add(key);
            return TextureUtils.getBlackTexture();
        }
        cache.put(key, loaded);
        texture.setDimensions(loaded.width, loaded.height);
        return loaded.id;
    }

    public void bind(Texture2D texture, int unit) {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, getTextureId(texture));
    }

    public void preload(Texture2D texture) {
        getTextureId(texture);
    }

    public void unload(Texture2D texture) {
        if (texture == null) {
            return;
        }
        TextureKey key = TextureKey.from(texture);
        ManagedTexture removed = cache.remove(key);
        if (removed != null) {
            removed.close();
        }
        failedTextures.remove(key);
        texture.setDimensions(-1, -1);
    }

    public void reloadAll() {
        for (ManagedTexture texture : cache.values()) {
            texture.close();
        }
        cache.clear();
        failedTextures.clear();
    }

    @Override
    public void close() {
        reloadAll();
    }

    private ManagedTexture loadTexture(TextureKey key) {
        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        try (InputStream in = Minecraft.getInstance().getResourceManager().getResource(key.location()).orElseThrow().open();
            NativeImage img = NativeImage.read(in)) {
            int width = img.getWidth();
            int height = img.getHeight();

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, key.generateMipmap() ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, key.wrapS());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, key.wrapT());

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, img.getPixelsRGBA());

            if (key.generateMipmap()) {
                glGenerateMipmap(GL_TEXTURE_2D);
            }
            return new ManagedTexture(id, width, height);
        } catch (Exception e) {
            glDeleteTextures(id);
            Paramagic.LOG.error("Failed to load texture: {}", key.location(), e);
            return null;
        } finally {
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }

    private record TextureKey(ResourceLocation location, boolean generateMipmap, int wrapS, int wrapT) {
        private static TextureKey from(Texture2D texture) {
            return new TextureKey(
                    texture.getLocation(),
                    texture.isGenerateMipmap(),
                    texture.getWrapS(),
                    texture.getWrapT()
            );
        }
    }

    private static final class ManagedTexture implements AutoCloseable {
        private final int id;
        private final int width;
        private final int height;

        private ManagedTexture(int id, int width, int height) {
            this.id = id;
            this.width = width;
            this.height = height;
        }

        @Override
        public void close() {
            glDeleteTextures(id);
        }
    }
}
