package com.qwaecd.paramagic.ui.inventory;

import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public interface IContainerScreen {
    void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type);
}
