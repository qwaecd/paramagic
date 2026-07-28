package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;

public class HeavyOperator extends ModifierOperator {
    private static final float GRAVITY_SCALE = 1.2f;
    private static final float LINEAR_DAMPING = 0.01f;

    public HeavyOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("heavy_operator"));
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
