package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public class PierceOperator extends ModifierOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("pierce_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.05f, 0.5f, 50)
    );

    private static final int PIERCE_BONUS = 1;

    public PierceOperator() {
        super(OP_ID, ModItems.PIERCE_OPERATOR);
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
    }
}