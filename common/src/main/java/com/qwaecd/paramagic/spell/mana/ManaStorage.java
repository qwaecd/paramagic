package com.qwaecd.paramagic.spell.mana;

import net.minecraft.world.entity.player.Player;

public interface ManaStorage {
    int getMana(Player player);

    void setMana(Player player, int mana);

    int getMaxMana(Player player);

    void setMaxMana(Player player, int maxMana);
}
