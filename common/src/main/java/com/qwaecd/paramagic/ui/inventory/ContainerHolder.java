package com.qwaecd.paramagic.ui.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerHolder implements InventoryHolder {
    private final Container container;

    public ContainerHolder(Container container) {
        this.container = container;
    }

    public Container getContainer() {
        return this.container;
    }

    @Override
    public int size() {
        return this.container.getContainerSize();
    }

    @Override
    public ItemStack getStackInSlot(int slotId) {
        return this.container.getItem(slotId);
    }

    @Override
    public void setStackInSlot(int slotId, ItemStack stack) {
        this.container.setItem(slotId, stack);
    }

    @Override
    public void onSlotChanged(Slot slot) {
        this.container.setChanged();
    }
}
