package com.qwaecd.paramagic.ui.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

public class UISlot extends Slot {
    public static final Container EMPTY_CONTAINER = new SimpleContainer(0);
    @Nonnull
    protected final InventoryHolder inventory;

    protected final int slotId;
    private boolean slotEnabled = true;
    private boolean draggable = true;

    public UISlot(InventoryHolder inv, int slotId) {
        super(EMPTY_CONTAINER, slotId, -123, -123);
        this.inventory = inv;
        this.slotId = slotId;
    }

    public int getSlotId() {
        return this.slotId;
    }

    @Nonnull
    public InventoryHolder getInventoryHolder() {
        return this.inventory;
    }

    public boolean isSlotEnabled() {
        return this.slotEnabled;
    }

    public void setSlotEnabled(boolean enabled) {
        this.slotEnabled = enabled;
    }

    public boolean isDraggable() {
        return this.draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.setChanged();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (!this.slotEnabled) {
            return false;
        }
        return this.inventory.isItemValid(this.slotId, stack);
    }

    @Override
    public ItemStack getItem() {
        if (!this.slotEnabled || this.slotId >= this.inventory.size()) {
            return ItemStack.EMPTY;
        }
        return this.inventory.getStackInSlot(this.slotId);
    }

    @Override
    public void set(ItemStack stack) {
        if (this.slotEnabled) {
            this.inventory.setStackInSlot(this.slotId, stack);
            this.setChanged();
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.inventory.onSlotChanged(this);
    }

    @Override
    public int getMaxStackSize() {
        return this.inventory.getSlotLimit(this.slotId);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
    }

    @Override
    public ItemStack remove(int amount) {
        if (!this.slotEnabled) return ItemStack.EMPTY;
        return this.inventory.extractItem(this.slotId, amount, false);
    }

    @Override
    public boolean mayPickup(Player player) {
        if (!this.slotEnabled) {
            return false;
        }
        return !this.inventory.extractItem(this.slotId, 1, true).isEmpty();
    }

    @Override
    public boolean isActive() {
        return this.slotEnabled;
    }

    @Override
    public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
        if (!this.slotEnabled) {
            return Optional.empty();
        }
        return super.tryRemove(count, decrement, player);
    }

    @Override
    public ItemStack safeTake(int count, int decrement, Player player) {
        if (!this.slotEnabled) {
            return ItemStack.EMPTY;
        }
        return super.safeTake(count, decrement, player);
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int increment) {
        if (!this.slotEnabled) {
            return stack;
        }
        return super.safeInsert(stack, increment);
    }
}
