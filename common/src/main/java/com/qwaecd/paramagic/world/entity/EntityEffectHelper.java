package com.qwaecd.paramagic.world.entity;

import com.qwaecd.paramagic.client.renderbase.prototype.SpherePrototype;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.geometricmask.DistortionGeometricMaskEffect;
import com.qwaecd.paramagic.core.render.geometricmask.GeometricEffectCaster;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.world.entity.projectile.GravityCollapseEntity;

@PlatformScope(PlatformScopeType.CLIENT)
public final class EntityEffectHelper {
    private EntityEffectHelper() {}

    public static void spawnGravityCollapseEffect(GravityCollapseEntity entity) {
        GeometricEffectCaster old = entity.getDistortionCaster();
        if (old != null) {
            ModRenderSystem.getInstance().removeGeometricEffectCaster(old);
        }
        DistortionGeometricMaskEffect effect = new DistortionGeometricMaskEffect()
                .setDistortionStrength(0.002f)
                .setInnerRadius(0.0018f)
                .setOuterRadius(1.0f)
                .setMaxOffset(0.4f);
        Transform transform = new Transform();
        transform.setPosition((float) entity.getX(), (float) entity.getY(), (float) entity.getZ());
        transform.setScale(entity.getDistortionRadius());
        GeometricEffectCaster caster = new GeometricEffectCaster(
                SpherePrototype.getINSTANCE().getMesh(),
                transform,
                effect
        );
        entity.setDistortionCaster(caster);
        ModRenderSystem.getInstance().addGeometricEffectCaster(caster);
    }

    public static void removeGravityCollapseEffect(GravityCollapseEntity entity) {
        GeometricEffectCaster caster = entity.getDistortionCaster();
        if (caster != null) {
            ModRenderSystem.getInstance().removeGeometricEffectCaster(caster);
        }
    }
}
