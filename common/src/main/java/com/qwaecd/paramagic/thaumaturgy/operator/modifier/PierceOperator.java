package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.DamagingProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.damage.ModDamageTypes;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;

public class PierceOperator extends ModifierOperator {
    private static final int PIERCE_BONUS = 1;

    public PierceOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("pierce_operator"));
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryApplyPierce(projectile));
        return true;
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
        this.tryApplyPierce(entity);
    }

    private void tryApplyPierce(ProjectileEntity projectile) {
        projectile.setMaxEntityPierceCount(projectile.getMaxEntityPierceCount() + PIERCE_BONUS);
        if (projectile instanceof DamagingProjectileEntity damagingProjectile) {
            final float amount = damagingProjectile.getProjectileDamage().amount() * 0.9f;
            damagingProjectile.setDamage(
                    ModDamageTypes.ARCANE_PROJECTILE_BYPASSES_COOLDOWN,
                    amount,
                    true
            );
        }
    }
}
