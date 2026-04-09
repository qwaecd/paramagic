package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.property.LifetimeCarrier;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public class ExtendLifetimeOperator extends ModifierOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("extend_lifetime_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.06f, 0.02f)
    );

    private static final float LIFETIME_SCALE = 1.33f;

    public ExtendLifetimeOperator() {
        super(OP_ID, ModItems.EXTEND_LIFETIME_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryExtendLifetime(projectile));
        return true;
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
        this.tryExtendLifetime(entity);
    }

    private void tryExtendLifetime(ProjectileEntity projectile) {
        if (projectile instanceof LifetimeCarrier carrier) {
            carrier.setLifetime(carrier.getLifetime() * LIFETIME_SCALE);
        }
    }
}
