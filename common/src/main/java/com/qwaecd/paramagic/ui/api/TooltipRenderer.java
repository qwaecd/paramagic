package com.qwaecd.paramagic.ui.api;

import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nonnull;

public interface TooltipRenderer {
    void renderTooltip(@Nonnull TooltipContent tooltip, GuiGraphics guiGraphics, int mouseX, int mouseY);
}
