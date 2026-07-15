package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public class BounceOperator extends ModifierOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("bounce_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.04f, 0.05f, 25)
    );

    private static final int BOUNCE_BONUS = 1;

    public BounceOperator() {
        super(OP_ID, ModItems.BOUNCE_OPERATOR);
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