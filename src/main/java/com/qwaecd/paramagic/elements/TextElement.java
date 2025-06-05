package com.qwaecd.paramagic.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public class TextElement extends Element {
    private String text;
    private String fontName;
    private int fontSize;
    private TextAlign align;

    public enum TextAlign {
        LEFT, CENTER, RIGHT
    }

    public TextElement(String text, String fontName, int fontSize) {
        this.text = text;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.align = TextAlign.CENTER;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, Vec3 centerPos, float partialTicks) {
        poseStack.pushPose();
        applyTransformations(poseStack);

        Font font = Minecraft.getInstance().font;
        int textColor = color.getRGB();

        float textWidth = font.width(text);
        float xOffset = 0;

        switch (align) {
            case CENTER: xOffset = -textWidth / 2f; break;
            case RIGHT: xOffset = -textWidth; break;
            case LEFT: default: xOffset = 0; break;
        }

        // Scale font size
        float fontScale = fontSize / 9f; // Default MC font size is ~9
        poseStack.scale(fontScale, fontScale, 1f);

        font.drawInBatch(text, xOffset / fontScale, 0, textColor, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 15728880);

        renderChildren(poseStack, buffer, centerPos, partialTicks);
        poseStack.popPose();
    }

    public void setText(String text) { this.text = text; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    public void setAlign(TextAlign align) { this.align = align; }
}