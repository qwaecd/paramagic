package com.qwaecd.paramagic.ui.inventory;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface InventoryHolder {
    int size();
    ItemStack getStackInSlot(int slotId);

    void setStackInSlot(int slotId, ItemStack stack);

    void onSlotChanged(Slot slot);
}
