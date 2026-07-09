package com.qwaecd.paramagic.thaumaturgy.spelltree;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public enum PlayerOffhandSpellTreeEditTarget implements SpellTreeEditTarget {
    INSTANCE;

    @Override
    @Nonnull
    public ItemStack getCrystalStack(@Nonnull ServerPlayer player) {
        return player.getOffhandItem();
    }

    @Override
    public void markChanged(@Nonnull ServerPlayer player) {
        player.getInventory().setChanged();
    }
}
