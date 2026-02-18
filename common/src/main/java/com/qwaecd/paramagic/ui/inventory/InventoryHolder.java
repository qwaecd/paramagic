package com.qwaecd.paramagic.ui.inventory;

import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import net.minecraft.world.item.ItemStack;

public interface InventoryHolder {
    int size();
    ItemStack getStackInSlot(int slotId);

    void setStackInSlot(int slotId, ItemStack stack);

    void onSlotChanged(UISlot slot);

    /**
     * 检查物品是否可以放入指定槽位。
     * 默认实现：允许所有物品。
     */
    default boolean isItemValid(int slotId, ItemStack stack) {
        return true;
    }

    /**
     * 从指定槽位提取物品。
     *
     * @param slotId   槽位索引
     * @param amount   提取数量
     * @param simulate 如果为true则仅模拟，不实际修改
     * @return 实际提取的物品
     */
    default ItemStack extractItem(int slotId, int amount, boolean simulate) {
        if (slotId < 0 || slotId >= this.size()) {
            return ItemStack.EMPTY;
        }
        ItemStack existing = this.getStackInSlot(slotId);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getCount());
        ItemStack result = existing.copy();
        result.setCount(toExtract);

        if (!simulate) {
            existing.shrink(toExtract);
            if (existing.isEmpty()) {
                this.setStackInSlot(slotId, ItemStack.EMPTY);
            }
        }
        return result;
    }

    /**
     * 获取槽位的最大堆叠数。
     */
    default int getSlotLimit(int slotId) {
        return 64;
    }

    void registerListener(InventoryListener listener);
    void removeListener(InventoryListener listener);
}
