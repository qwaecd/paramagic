package com.qwaecd.paramagic.core.render.post.buffer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;

import static org.lwjgl.opengl.GL30.*;
public class FramebufferUtils {
    /**
     * Copies the depth buffer content from the source RenderTarget to the destination FBO's depth attachment.
     * @param sourceMcFbo The main Minecraft render target
     * @param destModFbo  Your custom FBO
     * <p>
     * 将源RenderTarget的深度缓冲内容复制到目标FBO的深度附件。<p>
     * sourceMcFbo Minecraft的主渲染目标<p>
     * destModFbo 你的自定义FBO
     */
    public static void copyDepth(RenderTarget sourceMcFbo, SceneMRTFramebuffer destModFbo) {
        int width = Minecraft.getInstance().getWindow().getWidth();
        int height = Minecraft.getInstance().getWindow().getHeight();
        // 绑定读和写的FBO
        glBindFramebuffer(GL_READ_FRAMEBUFFER, sourceMcFbo.frameBufferId);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, destModFbo.getFboId());
        glBlitFramebuffer(
                0, 0, width, height, // 源区域
                0, 0, width, height, // 目标区域
                GL_DEPTH_BUFFER_BIT,
                GL_NEAREST
        );
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
