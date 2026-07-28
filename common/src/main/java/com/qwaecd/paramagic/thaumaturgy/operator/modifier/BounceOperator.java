package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;

public class BounceOperator extends ModifierOperator {
    private static final int BOUNCE_BONUS = 1;

    public BounceOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("bounce_operator"));
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryApplyBounce(projectile));
        return true;
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
        this.tryApplyBounce(entity);
    }

    private void tryApplyBounce(ProjectileEntity projectile) {
        projectile.setMaxBounceCount(projectile.getMaxBounceCount() + BOUNCE_BONUS);
    }
}