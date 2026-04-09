package com.qwaecd.paramagic.thaumaturgy.projectile.property;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;

public final class ProjectileAccess {

    public static void trySetLifetime(ProjectileEntity entity, float value) {
        if (entity instanceof LifetimeCarrier carrier) {
            carrier.setLifetime(value);
        }
    }

    public static void trySetBaseDamage(ProjectileEntity entity, float value) {
        if (entity instanceof DamageCarrier carrier) {
            carrier.setBaseDamage(value);
        }
    }
}
