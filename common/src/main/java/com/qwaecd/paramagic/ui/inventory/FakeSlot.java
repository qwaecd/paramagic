package com.qwaecd.paramagic.ui.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

public class FakeSlot extends Slot {
    public static final Container EMPTY_CONTAINER = new SimpleContainer(0);
    @Nonnull
    protected final InventoryHolder inventory;

    protected final int slotId;

    public FakeSlot(InventoryHolder inv, int slotId) {
        super(EMPTY_CONTAINER, slotId, -123, -123);
        this.inventory = inv;
        this.slotId = slotId;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        super.onTake(player, stack);
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack);
    }

    @Override
    public ItemStack getItem() {
        if (this.slotId >= this.inventory.size()) {
            return ItemStack.EMPTY;
        }

        return this.inventory.getStackInSlot(this.slotId);
    }

    @Override
    public boolean hasItem() {
        return super.hasItem();
    }

    @Override
    public void setByPlayer(ItemStack stack) {
        super.setByPlayer(stack);
    }

    /**
     * Helper method to put a stack in the slot.
     */
    @Override
    public void set(ItemStack stack) {
        this.inventory.setStackInSlot(this.slotId, stack);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.inventory.onSlotChanged(this);
    }

    @Override
    @Nullable
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return super.getNoItemIcon();
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
     */
    @Override
    public ItemStack remove(int amount) {
        return super.remove(amount);
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    @Override
    public boolean mayPickup(Player player) {
        return super.mayPickup(player);
    }

    @Override
    public boolean isActive() {
        return super.isActive();
    }

    @Override
    public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
        return super.tryRemove(count, decrement, player);
    }

    @Override
    public ItemStack safeTake(int count, int decrement, Player player) {
        return super.safeTake(count, decrement, player);
    }

    @Override
    public ItemStack safeInsert(ItemStack stack) {
        return super.safeInsert(stack);
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int increment) {
        return super.safeInsert(stack, increment);
    }

    @Override
    public boolean allowModification(Player player) {
        return super.allowModification(player);
    }
}
