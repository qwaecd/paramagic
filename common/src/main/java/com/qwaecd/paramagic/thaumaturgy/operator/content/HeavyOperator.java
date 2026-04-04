package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileGravityMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileLinearDampingMutable;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public class HeavyOperator extends ProjectileOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("heavy_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.2f, 0.5f)
    );

    private static final float GRAVITY_SCALE = 2.2f;
    private static final float LINEAR_DAMPING = 0.01f;

    public HeavyOperator() {
        super(OP_ID, ModItems.HEAVY_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryApplyHeavy(projectile));
        return true;
    }

    private void tryApplyHeavy(ProjectileEntity projectile) {
        if (projectile instanceof ProjectileGravityMutable gravityMutable) {
            gravityMutable.setGravityScale(Math.max(gravityMutable.getGravityScale(), GRAVITY_SCALE));
        }
        if (projectile instanceof ProjectileLinearDampingMutable dampingMutable) {
            dampingMutable.setLinearDamping(Math.max(dampingMutable.getLinearDamping(), LINEAR_DAMPING));
        }
    }
}
