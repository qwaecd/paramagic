package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.property.LifetimeCarrier;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;

public class ShortenLifetimeOperator extends ModifierOperator {
    private static final float LIFETIME_SCALE = 0.66f;

    public ShortenLifetimeOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("shorten_lifetime_operator"));
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryShortenLifetime(projectile));
        return true;
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
        this.tryShortenLifetime(entity);
    }

    private void tryShortenLifetime(ProjectileEntity projectile) {
        if (projectile instanceof LifetimeCarrier carrier) {
            carrier.setLifetime(carrier.getLifetime() * LIFETIME_SCALE);
        }
    }
}
