package com.qwaecd.paramagic.spell.view;

import com.qwaecd.paramagic.core.render.TransformSample;

import javax.annotation.Nullable;

@SuppressWarnings({"LombokSetterMayBeUsed"})
public class HybridCasterSource implements CasterTransformSource {
    private CasterTransformSource fallback;
    @Nullable
    private CasterTransformSource primary;

    private final TransformSample last = new TransformSample();

    public HybridCasterSource(@Nullable CasterTransformSource primary, CasterTransformSource fallback) {
        this.primary = primary;
        this.fallback = fallback;
        if (primary != null) {
            primary.applyTo(this.last);
        } else if (fallback != null) {
            fallback.applyTo(this.last);
        }
    }

    public void setPrimary(@Nullable CasterTransformSource primary) {
        this.primary = primary;
    }

    public void setFallback(CasterTransformSource fallback) {
        this.fallback = fallback;
    }

    @Override
    public void applyTo(TransformSample dist) {
        if (this.primary != null) {
            this.primary.applyTo(this.last);
        } else if (this.fallback != null) {
            this.fallback.applyTo(this.last);
        }
        dist.set(this.last);
    }

    public static HybridCasterSource create(@Nullable CasterTransformSource primary, CasterTransformSource fallback) {
        return new HybridCasterSource(primary, fallback);
    }
}
