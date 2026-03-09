package com.qwaecd.paramagic.ui.api;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

@FunctionalInterface
public interface WidgetProvider<T extends GuiEventListener & NarratableEntry> {
    T get();
}
