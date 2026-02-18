package com.qwaecd.paramagic.ui.inventory;

import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PlayerInventoryHolder implements InventoryHolder {
    private final List<InventoryListener> listeners = new ArrayList<>();
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
    public void onSlotChanged(UISlot slot) {
        this.inventory.setChanged();
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
