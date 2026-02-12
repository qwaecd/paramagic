package com.qwaecd.paramagic.spell.view;

import com.qwaecd.paramagic.core.render.TransformSample;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@SuppressWarnings({"LombokSetterMayBeUsed"})
public class HybridCasterSource implements CasterTransformSource {
    private Entity fallback;
    @Nullable
    private Entity primary;

    private final TransformSample last = new TransformSample();

    public HybridCasterSource(@Nullable Entity primary, Entity fallback) {
        this.primary = primary;
        this.fallback = fallback;
        if (primary != null) {
            this.last.fromEntity(primary);
        } else if (fallback != null) {
            this.last.fromEntity(fallback);
        }
    }

    public int getCasterNetId() {
        if (primary != null) {
            return primary.getId();
        } else if (fallback != null) {
            return fallback.getId();
        }
        throw new NullPointerException("Both primary and fallback caster sources are null");
    }

    public void setPrimary(@Nullable Entity primary) {
        this.primary = primary;
    }

    public void setFallback(Entity fallback) {
        this.fallback = fallback;
    }

    @Override
    public TransformSample applyTo(TransformSample dist) {
        if (primary != null) {
            this.last.fromEntity(primary);
        } else if (fallback != null) {
            this.last.fromEntity(fallback);
        }
        dist.set(this.last);
        return dist;
    }

    public static HybridCasterSource create(@Nullable Entity primary, Entity fallback) {
        return new HybridCasterSource(primary, fallback);
    }
}
