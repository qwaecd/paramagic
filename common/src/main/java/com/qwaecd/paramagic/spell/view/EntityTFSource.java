package com.qwaecd.paramagic.spell.view;

import com.qwaecd.paramagic.core.render.TransformSample;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class EntityTFSource implements CasterTransformSource {
    @Nullable
    private final Entity source;
    private final TransformSample last = new TransformSample();

    private EntityTFSource(@Nullable Entity entity) {
        this.source = entity;
        if (entity != null)
            this.last.fromEntity(entity);
    }

    @Override
    public void applyTo(TransformSample dist) {
        if (this.source != null && this.source.isAlive()) {
            this.last.fromEntity(this.source);
        }
        dist.set(this.last);
    }

    public static EntityTFSource create(Entity entity) {
        return new EntityTFSource(entity);
    }
}
