package com.qwaecd.paramagic.world.entity.projectile;

public record CollisionResolution(HitDecision action) {
    public CollisionResolution {
        if (action == HitDecision.IGNORE) {
            throw new IllegalArgumentException("IGNORE is not a resolved collision action");
        }
    }
}
