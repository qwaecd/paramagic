package com.qwaecd.paramagic.world.explosion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public final class ExplosionVisualEvent {
    public final ServerLevel level;
    public final Vec3 center;
    public final float radius;
    @Nullable
    public final Entity source;
    public final int destroyedBlocks;
    public final boolean completed;

    ExplosionVisualEvent(CustomExplosionConfig config, int destroyedBlocks, boolean completed) {
        this.level = config.level;
        this.center = config.center;
        this.radius = config.radius;
        this.source = config.source;
        this.destroyedBlocks = destroyedBlocks;
        this.completed = completed;
    }
}
