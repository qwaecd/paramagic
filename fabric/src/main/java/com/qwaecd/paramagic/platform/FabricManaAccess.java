package com.qwaecd.paramagic.platform;

import com.mojang.serialization.Codec;
import com.qwaecd.paramagic.spell.mana.ManaAccess;
import com.qwaecd.paramagic.spell.mana.ManaStorage;
import com.qwaecd.paramagic.tools.ModRL;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.player.Player;

public final class FabricManaAccess implements ManaStorage {
    private static final AttachmentType<Integer> MANA = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> ManaAccess.DEFAULT_MAX_MANA)
            .buildAndRegister(ModRL.inModSpace("mana"));
    private static final AttachmentType<Integer> MAX_MANA = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> ManaAccess.DEFAULT_MAX_MANA)
            .buildAndRegister(ModRL.inModSpace("max_mana"));

    @Override
    public int getMana(Player player) {
        return target(player).getAttachedOrCreate(MANA);
    }

    @Override
    public void setMana(Player player, int mana) {
        target(player).setAttached(MANA, mana);
    }

    @Override
    public int getMaxMana(Player player) {
        return target(player).getAttachedOrCreate(MAX_MANA);
    }

    @Override
    public void setMaxMana(Player player, int maxMana) {
        target(player).setAttached(MAX_MANA, maxMana);
    }

    private static AttachmentTarget target(Player player) {
        return player;
    }
}
