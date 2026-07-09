package com.qwaecd.paramagic.ui.screen;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import javax.annotation.Nullable;

public interface NativeWidgetHostScreen {
    @Nullable
    GuiEventListener getFocused();

    void setFocused(@Nullable GuiEventListener listener);

    boolean forwardVanillaMouseClicked(double mouseX, double mouseY, int button);

    boolean forwardVanillaMouseReleased(double mouseX, double mouseY, int button);

    <W extends GuiEventListener & Renderable & NarratableEntry> void addNativeRenderableWidget(W widget);

    void removeNativeWidget(GuiEventListener widget);
}
