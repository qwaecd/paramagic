package com.qwaecd.paramagic.ui.inventory;

import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ContainerHolder implements InventoryHolder {
    private final List<InventoryListener> listeners = new ArrayList<>();
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
    public void onSlotChanged(UISlot slot) {
        this.container.setChanged();
        for (InventoryListener listener : this.listeners) {
            listener.onInventoryChanged(this, slot);
        }
    }

    @Override
    public void registerListener(InventoryListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(InventoryListener listener) {
        this.listeners.remove(listener);
    }
}
