package com.qwaecd.paramagic.ui;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qwaecd.paramagic.ui.api.UIRenderBackend;
import com.qwaecd.paramagic.ui.util.Rect;
import com.qwaecd.paramagic.ui.util.Sprite;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class MCRenderBackend implements UIRenderBackend {
    private GuiGraphics guiGraphics;
    private final Font font;

    public MCRenderBackend(GuiGraphics guiGraphics, Font font) {
        this.guiGraphics = guiGraphics;
        this.font = font;
    }

    @Override
    public void pushClipRect(Rect rect) {
        // mc 要的是两个点的位置, 而不是一个点和矩形的宽高
        this.guiGraphics.enableScissor((int) rect.x, (int) rect.y, (int) (rect.x + rect.w), (int) (rect.y + rect.h));
    }

    @Override
    public void popClipRect() {
        this.guiGraphics.disableScissor();
    }

    @Override
    public void vLine(int x, int minY, int maxY, int color) {
        this.guiGraphics.vLine(x, minY, maxY, color);
    }

    @Override
    public void hLine(int minX, int maxX, int y, int color) {
        this.guiGraphics.hLine(minX, maxX, y, color);
    }

    @Override
    public int getTextWidth(String text) {
        return this.font.width(text);
    }

    @Override
    public int getTextWidth(Component text) {
        return this.font.width(text);
    }

    @Override
    public int getLineHeight() {
        return this.font.lineHeight;
    }

    /**
     * 在指定位置渲染精灵图.
     * @param sprite 精灵图实例.
     * @param x 绘制区域左上角的 x 坐标.
     * @param y 绘制区域左上角的 y 坐标.
     */
    @Override
    public void renderSprite(Sprite sprite, int x, int y) {
        this.guiGraphics.blit(
                sprite.texture,
                x, y,
                sprite.u, sprite.v,
                sprite.width, sprite.height,
                sprite.texWidth, sprite.texHeight
        );
    }

    @Override
    public void blit(
            ResourceLocation atlasLocation,
            int x, int y,
            int width, int height,
            float uOffset, float vOffset,
            int uWidth, int vHeight,
            int textureWidth, int textureHeight
    ) {
        this.guiGraphics.blit(
                atlasLocation,
                x, y,
                width, height,
                uOffset, vOffset,
                uWidth, vHeight,
                textureWidth, textureHeight
        );
    }

    @Override
    public void renderItem(ItemStack stack, int x, int y) {
        this.guiGraphics.renderItem(stack, x, y);
    }

    @Override
    public void renderItemDecorations(ItemStack stack, int x, int y, @Nullable String text) {
        this.guiGraphics.renderItemDecorations(this.font, stack, x, y, text);
    }

    @Override
    public void drawQuad(Rect rect, int color) {
        this.guiGraphics.fill((int) rect.x, (int) rect.y, (int) (rect.x + rect.w), (int) (rect.y + rect.h), color);
    }

    @Override
    public void fillBilinearGradient(int x, int y, int w, int h, int topLeft, int topRight, int bottomRight, int bottomLeft) {
        VertexConsumer consumer = this.guiGraphics.bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix = this.guiGraphics.pose().last().pose();

        putVertex(consumer, matrix, x, y, topLeft);
        putVertex(consumer, matrix, x, y + h, bottomLeft);
        putVertex(consumer, matrix, x + w, y + h, bottomRight);
        putVertex(consumer, matrix, x + w, y, topRight);

        this.guiGraphics.flush();
    }

    @Override
    public void fill(int minX, int minY, int maxX, int maxY, int color) {
        this.guiGraphics.fill(minX, minY, maxX, maxY, color);
    }

    @Override
    public int drawText(Component text, int x, int y, int color, boolean dropShadow) {
        return this.guiGraphics.drawString(this.font, text, x, y, color, dropShadow);
    }

    @Override
    public void drawCenteredText(Component text, float centerX, float y, int color) {
        this.guiGraphics.drawCenteredString(this.font, text, (int) centerX, (int) y, color);
    }

    @Override
    public void renderOutline(int x, int y, int w, int h, int color) {
        this.guiGraphics.renderOutline(x, y, w, h, color);
    }

    private static void putVertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, int color) {
        consumer.vertex(matrix, x, y, 0.0f)
                .color(
                        (float) FastColor.ARGB32.red(color) / 255.0f,
                        (float) FastColor.ARGB32.green(color) / 255.0f,
                        (float) FastColor.ARGB32.blue(color) / 255.0f,
                        (float) FastColor.ARGB32.alpha(color) / 255.0f
                )
                .endVertex();
    }

    public void setGuiGraphics(GuiGraphics gg) {
        this.guiGraphics = gg;
    }
}
