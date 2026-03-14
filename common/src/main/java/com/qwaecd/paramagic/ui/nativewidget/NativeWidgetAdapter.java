package com.qwaecd.paramagic.ui.nativewidget;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import javax.annotation.Nonnull;

public interface NativeWidgetAdapter<N extends NativeWidgetNode<W, N>, W extends GuiEventListener & Renderable & NarratableEntry> {
    @Nonnull
    W createWidget(@Nonnull N node);

    void syncWidget(@Nonnull N node, @Nonnull W widget);

    default void onRemoved(@Nonnull N node, @Nonnull W widget) {
    }
}
