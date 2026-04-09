package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public class HeavyOperator extends ModifierOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("heavy_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.04f, 0.05f)
    );

    private static final float GRAVITY_SCALE = 1.2f;
    private static final float LINEAR_DAMPING = 0.01f;

    public HeavyOperator() {
        super(OP_ID, ModItems.HEAVY_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryApplyHeavy(projectile));
        return true;
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
        this.tryApplyHeavy(entity);
    }

    private void tryApplyHeavy(ProjectileEntity projectile) {
        PhysicsProvider physics = projectile.physics();
        physics.setGravityScale(physics.getGravityScale() * GRAVITY_SCALE);
        physics.setDragCoefficient(physics.getDragCoefficient() + LINEAR_DAMPING);
    }
}
