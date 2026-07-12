package com.qwaecd.paramagic.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class WandItem extends Item {
    public WandItem(Properties properties) {
        super(properties);
    }

    public static boolean shouldSkipSlowdown(ItemStack itemStack) {
        return itemStack.getItem() instanceof WandItem wandItem && wandItem.shouldSkipSlowdown();
    }

    protected boolean shouldSkipSlowdown() {
        return true;
    }
}
