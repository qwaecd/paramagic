package com.qwaecd.paramagic.spell.caster;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public final class ManaAccess {
    public static final int DEFAULT_MAX_MANA = 1000;
    private static ManaStorage storage;

    private ManaAccess() {
    }

    /** Called exactly once by the active loader's mod entry point. */
    public static synchronized void initialize(ManaStorage manaStorage) {
        if (storage != null) {
            throw new IllegalStateException("ManaAccess has already been initialized");
        }
        storage = Objects.requireNonNull(manaStorage, "manaStorage");
    }

    public static int getMana(Player player) {
        Objects.requireNonNull(player, "player");
        return clamp(backend().getMana(player), getMaxMana(player));
    }

    public static int getMaxMana(Player player) {
        Objects.requireNonNull(player, "player");
        return Math.max(0, backend().getMaxMana(player));
    }

    public static void setMana(Player player, int mana) {
        Objects.requireNonNull(player, "player");
        ManaStorage backend = backend();
        int normalizedMana = clamp(mana, getMaxMana(player));
        if (clamp(backend.getMana(player), getMaxMana(player)) == normalizedMana) {
            return;
        }
        backend.setMana(player, normalizedMana);
        syncIfServerPlayer(player);
    }

    public static void setMaxMana(Player player, int maxMana) {
        Objects.requireNonNull(player, "player");
        int normalizedMaxMana = Math.max(0, maxMana);
        ManaStorage backend = backend();
        int currentMana = clamp(backend.getMana(player), getMaxMana(player));
        int updatedMana = Math.min(currentMana, normalizedMaxMana);
        if (getMaxMana(player) == normalizedMaxMana && currentMana == updatedMana) {
            return;
        }
        backend.setMaxMana(player, normalizedMaxMana);
        backend.setMana(player, updatedMana);
        syncIfServerPlayer(player);
    }

    public static boolean tryConsumeMana(Player player, int amount) {
        int current = getMana(player);
        if (current < amount) {
            return false;
        }
        // 负数就相加
        setMana(player, current - amount);
        return true;
    }

    private static int clamp(int value, int max) {
        return Math.max(0, Math.min(value, max));
    }

    private static ManaStorage backend() {
        if (storage == null) {
            throw new IllegalStateException("ManaAccess has not been initialized by the active mod entry point");
        }
        return storage;
    }

    private static void syncIfServerPlayer(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            ManaSync.sync(serverPlayer);
        }
    }
}
