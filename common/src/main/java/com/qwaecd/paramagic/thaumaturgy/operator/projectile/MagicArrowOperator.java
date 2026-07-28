package com.qwaecd.paramagic.thaumaturgy.operator.projectile;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.world.entity.projectile.MagicArrowProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MagicArrowOperator extends ProjectileOperator {
    public MagicArrowOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("magic_arrow_operator"));
    }

    @Override
    public boolean apply(ParaContext context) {
        MagicArrowProjectile projectile = new MagicArrowProjectile(context.level);
        SpellCaster caster = context.caster;
        Entity casterEntity = context.level.getEntity(caster.getEntityNetworkId());

        final float strength = 1.0f;
        Vec3 position = caster.eyePosition();
        Vec3 forwarded = caster.forwardVector();
        projectile.setPosition((float) position.x, (float) position.y - 0.1f, (float) position.z);
        if (casterEntity != null) {
            projectile.setOwner(casterEntity);
        }
        projectile.setVelocity(forwarded.x * strength, forwarded.y * strength, forwarded.z * strength);
        projectile.setInaccuracy(10.0f);
        context.addProjectile(projectile);
        return true;
    }
}
