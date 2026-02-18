package com.qwaecd.paramagic.ui.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class PlayerInventoryHolder implements InventoryHolder {
    @Nonnull
    private final Inventory inventory;

    public PlayerInventoryHolder(@Nonnull Inventory inv) {
        this.inventory = inv;
    }

    @Nonnull
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public int size() {
        return this.inventory.getContainerSize();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slotId) {
        return this.inventory.getItem(slotId);
    }

    @Override
    public void setStackInSlot(int slotId, ItemStack stack) {
        this.inventory.setItem(slotId, stack);
    }

    @Override
    public void onSlotChanged(Slot slot) {
        this.inventory.setChanged();
    }
}
