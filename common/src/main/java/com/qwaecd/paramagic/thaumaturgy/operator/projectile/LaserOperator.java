package com.qwaecd.paramagic.thaumaturgy.operator.projectile;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.thaumaturgy.DamagingProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.damage.ModDamageTypes;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.world.entity.projectile.LaserProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class LaserOperator extends ProjectileOperator {
    public LaserOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("laser_operator"));
    }

    @Override
    public boolean apply(ParaContext context) {
        DamagingProjectileEntity projectile = new LaserProjectile(context.level);
        projectile.setDamage(ModDamageTypes.ARCANE_PROJECTILE, 5.0f);

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
