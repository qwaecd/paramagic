package com.qwaecd.paramagic.thaumaturgy.operator.projectile;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.entity.projectile.LaserProjectile;
import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class LaserOperator extends ProjectileOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("laser_operator"),
            new ParaOpId.Properties(OperatorType.PROJECTILE, -0.02f, 0.02f)
    );

    public LaserOperator() {
        super(OP_ID, ModItems.LASER_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        ProjectileEntity projectile = new LaserProjectile(context.level);

        SpellCaster caster = context.caster;
        Entity casterEntity = context.level.getEntity(caster.getEntityNetworkId());
        projectile.setOwner(casterEntity);

        Vec3 position = caster.eyePosition();
        projectile.setPosition((float) position.x, (float) position.y - 0.08f, (float) position.z);

        PhysicsProvider physics = projectile.physics();
        Vec3 forwarded = caster.forwardVector();
        final float strength = 1.5f;
        physics.setVelocity(forwarded.x * strength, forwarded.y * strength, forwarded.z * strength);
        projectile.setInaccuracy(10.0f);
        context.addProjectile(projectile);
        return true;
    }
}
