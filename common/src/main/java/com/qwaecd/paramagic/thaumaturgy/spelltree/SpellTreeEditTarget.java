package com.qwaecd.paramagic.thaumaturgy.spelltree;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface SpellTreeEditTarget {
    @Nonnull
    ItemStack getCrystalStack(@Nonnull ServerPlayer player);

    void markChanged(@Nonnull ServerPlayer player);
}
