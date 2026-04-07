package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileGravityMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileLinearDampingMutable;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public class WeightlessOperator extends ModifierOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("weightless_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.02f, 0.05f)
    );

    private static final float GRAVITY_SCALE = 0.15f;
    private static final float LINEAR_DAMPING = 0.0f;

    public WeightlessOperator() {
        super(OP_ID, ModItems.WEIGHTLESS_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryApplyWeightless(projectile));
        return true;
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
        this.tryApplyWeightless(entity);
    }

    private void tryApplyWeightless(ProjectileEntity projectile) {
        if (projectile instanceof ProjectileGravityMutable gravityMutable) {
            gravityMutable.setGravityScale(Math.min(gravityMutable.getGravityScale(), GRAVITY_SCALE));
        }
        if (projectile instanceof ProjectileLinearDampingMutable dampingMutable) {
            dampingMutable.setLinearDamping(Math.min(dampingMutable.getLinearDamping(), LINEAR_DAMPING));
        }
    }
}
