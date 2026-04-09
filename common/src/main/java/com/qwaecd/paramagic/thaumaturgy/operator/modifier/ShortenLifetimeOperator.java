package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.property.LifetimeCarrier;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public class ShortenLifetimeOperator extends ModifierOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("shorten_lifetime_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, -0.02f, -0.02f)
    );

    private static final float LIFETIME_SCALE = 0.66f;

    public ShortenLifetimeOperator() {
        super(OP_ID, ModItems.SHORTEN_LIFETIME_OPERATOR);
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
