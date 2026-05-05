package com.qwaecd.paramagic.world.explosion;

import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class ExplosionDropCallbacks {
    private ExplosionDropCallbacks() {
    }

    public static void dropContainerContents(ExplosionBlockEvent event) {
        BlockEntity blockEntity = event.level.getBlockEntity(event.pos);
        if (!(blockEntity instanceof Container container)) {
            return;
        }

        Containers.dropContents(event.level, event.pos, container);
        container.clearContent();
        event.level.updateNeighbourForOutputSignal(event.pos, event.state.getBlock());
    }
}
