package com.qwaecd.paramagic.ui.inventory;

import net.minecraft.world.inventory.ClickType;

public interface IContainerScreen {
    void slotClicked(UISlot slot, int mouseButton, ClickType type);
}
