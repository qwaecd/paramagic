package com.qwaecd.paramagic.ui.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface TooltipRenderer {
    void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY);
    void renderTooltipWithItem(@Nonnull ItemStack itemStack, GuiGraphics guiGraphics, int mouseX, int mouseY);
}
