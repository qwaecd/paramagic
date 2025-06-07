package com.qwaecd.paramagic.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.resource.ModResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class RuneElement extends Element {
    private ResourceLocation texture = ModResource.DEFAULT_MAGIC_CIRCLE;
    private String glyph;
    private float size;
    private boolean useTexture;

    public RuneElement(ResourceLocation texture, float size) {
        this.texture = texture;
        this.size = size;
        this.useTexture = true;
    }

    public RuneElement(String glyph, float size) {
        this.glyph = glyph;
        this.size = size;
        this.useTexture = false;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, Vec3 centerPos, float partialTicks) {
        poseStack.pushPose();
        applyTransformations(poseStack);

        if (useTexture && texture != null) {
            // Render texture-based rune
            RenderSystem.setShaderTexture(0, texture);
            // Texture rendering code would go here
        } else if (glyph != null) {
            // Render glyph-based rune
            Font font = Minecraft.getInstance().font;
            int textColor = color.getRGB();
            float textWidth = font.width(glyph);

            poseStack.scale(size / 9f, size / 9f, 1f);
            font.drawInBatch(glyph, -textWidth / 2f / (size / 9f), 0, textColor, false,
                    poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 15728880);
        }

        renderChildren(poseStack, buffer, centerPos, partialTicks);
        poseStack.popPose();
    }

    @Override
    public ElementType getElementType() {
        return ElementType.RUNE;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
        this.useTexture = true;
    }

    public  ResourceLocation getResourceLocation() {
        return texture;
    }
    public String getGlyph() {
        return glyph;
    }
    public float getSize() {
        return size;
    }
    public boolean isUseTexture() {
        return useTexture;
    }

    public void setGlyph(String glyph) {
        this.glyph = glyph;
        this.useTexture = false;
    }
    public void setSize(float size) { this.size = size; }
}
