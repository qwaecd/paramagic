package com.qwaecd.paramagic.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.resource.ModResource;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GuideBookScreen extends Screen {
    public GuideBookScreen() {
        super(Component.literal("gui"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        PoseStack poseStack = guiGraphics.pose();

        // Save current transformation
        poseStack.pushPose();

        // Reset scaling to get actual pixel coordinates
        float scale = (float)this.minecraft.getWindow().getGuiScale();
        poseStack.scale(1.0f / scale, 1.0f / scale, 1.0f);

        // Get actual window dimensions
        int windowWidth = this.minecraft.getWindow().getWidth();
        int windowHeight = this.minecraft.getWindow().getHeight();

        guiGraphics.blit(
                ModResource.GUIDE_BOOK_SCREEN,
                windowWidth/3, windowHeight/3,
                0,
                0,0,
                windowWidth/3, windowWidth/3,
                windowWidth, windowHeight
        );

//        guiGraphics.drawCenteredString(
//                this.font,
//                "Custom Screen",
//                this.width / 2,
//                20,
//                0xFFFFFF
//        );

        poseStack.popPose();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
