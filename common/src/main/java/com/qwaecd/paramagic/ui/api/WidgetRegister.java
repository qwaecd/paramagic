package com.qwaecd.paramagic.ui.api;

import net.minecraft.client.gui.components.events.GuiEventListener;

public interface WidgetRegister {
    void addMCWidget(WidgetProvider<?> widgetProvider);
    void removeMCWidget(GuiEventListener widget);
}
