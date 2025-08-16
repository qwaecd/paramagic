package com.qwaecd.paramagic.core.render.texture;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;

public final class TextureUtils {
    private static int blackTextureId = -1;
    private TextureUtils() {
    }
    public static int getBlackTexture() {
        if (blackTextureId == -1) {
            blackTextureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, blackTextureId);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            ByteBuffer blackPixel = MemoryUtil.memAlloc(4);
            blackPixel.put((byte) 0).put((byte) 0).put((byte) 0).put((byte) 255);
            blackPixel.flip();

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, blackPixel);
            MemoryUtil.memFree(blackPixel);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        return blackTextureId;
    }

}
