package com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import javax.annotation.Nullable;

public final class ProjectileRuntimeModifierContext {
    private final ProjectileEntity projectile;
    private final Entity entity;
    private final Level level;
    private final Vec3 position;
    private final Vector3d velocity;
    @Nullable
    private final Entity owner;
    private final int age;

    public ProjectileRuntimeModifierContext(
            ProjectileEntity projectile,
            Entity entity,
            Level level,
            Vec3 position,
            Vector3d velocity,
            Entity owner,
            int age
    ) {
        this.projectile = projectile;
        this.entity = entity;
        this.level = level;
        this.position = position;
        this.velocity = velocity;
        this.owner = owner;
        this.age = age;
    }

    public ProjectileEntity getProjectile() {
        return this.projectile;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public Level getLevel() {
        return this.level;
    }

    public Vec3 getPosition() {
        return this.position;
    }

    public Vector3d getVelocity() {
        return new Vector3d(this.velocity);
    }

    public Vector3d getVelocity(Vector3d dest) {
        return dest.set(this.velocity);
    }

    @Nullable
    public Entity getOwner() {
        return this.owner;
    }

    public int getAge() {
        return this.age;
    }
}
