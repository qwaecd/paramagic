package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.entity.projectile.MagicArrowProjectile;
import com.qwaecd.paramagic.world.item.ModItems;

public class MagicArrowOperator extends ProjectileOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(ModRL.inModSpace("magic_arrow_operator"), OperatorType.FLOW);
    public MagicArrowOperator() {
        super(OP_ID, ModItems.MAGIC_ARROW_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        MagicArrowProjectile projectile = new MagicArrowProjectile(context.level);
        context.addProjectile(projectile);
        return true;
    }
}
