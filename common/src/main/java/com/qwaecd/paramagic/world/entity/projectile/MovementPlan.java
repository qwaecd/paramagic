package com.qwaecd.paramagic.world.entity.projectile;

import net.minecraft.world.phys.Vec3;

import java.util.Objects;

/**
 * The complete movement for one tick. The end position is always derived from
 * {@code start + delta}, so callers cannot provide contradictory path data.
 */
public record MovementPlan(Vec3 start, Vec3 delta) {
    public MovementPlan {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(delta, "delta");
    }

    public Vec3 end() {
        return this.start.add(this.delta);
    }
}
