package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;
import org.joml.Vector3d;

public class AccelerateOperator extends ModifierOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("accelerate_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.04f, 0.03f)
    );

    private static final float IMPULSE = 0.5f;

    public AccelerateOperator() {
        super(OP_ID, ModItems.ACCELERATE_OPERATOR);
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
        Vector3d velocity = physics.getVelocity(new Vector3d()).normalize(IMPULSE);
        physics.addVelocity(velocity.x, velocity.y, velocity.z);
    }
}
