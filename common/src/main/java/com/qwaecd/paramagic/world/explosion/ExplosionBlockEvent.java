package com.qwaecd.paramagic.world.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class ExplosionBlockEvent {
    public final ServerLevel level;
    public final Vec3 center;
    public final BlockPos pos;
    public final BlockState state;
    public final float distance;
    public final float distanceFactor;
    public final float power;

    ExplosionBlockEvent(ServerLevel level, Vec3 center, BlockPos pos, BlockState state, float distance, float distanceFactor, float power) {
        this.level = level;
        this.center = center;
        this.pos = pos;
        this.state = state;
        this.distance = distance;
        this.distanceFactor = distanceFactor;
        this.power = power;
    }
}
