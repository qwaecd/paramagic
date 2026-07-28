package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.PhysicsMath;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import org.joml.Vector3d;

public class AccelerateOperator extends ModifierOperator {
    private static final float IMPULSE = 0.5f;

    public AccelerateOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("accelerate_operator"));
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryAccelerate(projectile));
        return true;
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
        this.tryAccelerate(entity);
    }

    private void tryAccelerate(ProjectileEntity projectile) {
        PhysicsProvider physics = projectile.physics();
        Vector3d velocity = physics.getVelocity(new Vector3d());
        Vector3d impulse = new Vector3d();
        if (!PhysicsMath.tryNormalize(velocity, IMPULSE, impulse)) {
            return;
        }
        physics.addVelocity(impulse.x, impulse.y, impulse.z);
    }
}
