package com.qwaecd.paramagic.thaumaturgy.spelltree;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

@FunctionalInterface
public interface SpellTreeDeletionHandler {
    SpellTreeDeletionHandler NO_OP = (player, level, removedNodes) -> {
    };

    void onDeleteSubtree(
            @Nonnull ServerPlayer player,
            @Nonnull Level level,
            @Nonnull List<SpellNodeData> removedNodes
    );
}
