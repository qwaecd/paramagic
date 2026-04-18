package com.qwaecd.paramagic.world.entity.projectile;

import net.minecraft.world.phys.Vec3;

public final class MovementPlan {
    public final Vec3 start;
    public final Vec3 delta;
    public final Vec3 intendedEnd;

    public MovementPlan(Vec3 start, Vec3 delta, Vec3 intendedEnd) {
        this.start = start;
        this.delta = delta;
        this.intendedEnd = intendedEnd;
    }
}
