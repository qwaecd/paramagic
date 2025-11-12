package com.qwaecd.paramagic.core.accessor;

import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

public class EntityAccessor {
    private final Entity source;

    public EntityAccessor(Entity entity) {
        this.source = entity;
    }

    public Vector3f getEyePosition() {
        return this.source.getEyePosition().toVector3f();
    }

    public Vector3f getLookAngle() {
        return this.source.getLookAngle().toVector3f();
    }
}
