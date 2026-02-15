package com.qwaecd.paramagic.thaumaturgy.operator;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface OperatorItemProvider {
    @Nonnull
    ItemStack createOperatorItem();
}
