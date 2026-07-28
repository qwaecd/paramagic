package com.qwaecd.paramagic.thaumaturgy.operator.projectile;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.world.entity.projectile.GravityCollapseEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;


public class GravityCollapseOperator extends ProjectileOperator {
    public GravityCollapseOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("gravity_collapse_operator"));
    }

    @Override
    public boolean apply(ParaContext context) {
        GravityCollapseEntity projectile = new GravityCollapseEntity(context.level);
        SpellCaster caster = context.caster;
        Entity casterEntity = context.level.getEntity(caster.getEntityNetworkId());

        final float strength = 0.1f;
        Vec3 eyePosition = caster.eyePosition();
        Vec3 forwarded = caster.forwardVector();
        Vector3f pos = new Vector3f((float) eyePosition.x, (float) eyePosition.y, (float) eyePosition.z);
        pos.add(forwarded.toVector3f().normalize(0.5f));
        projectile.setPosition(pos);
        if (casterEntity != null) {
            projectile.setOwner(casterEntity);
        }
        projectile.setVelocity((float) forwarded.x * strength, (float) forwarded.y * strength, (float) forwarded.z * strength);
        context.addProjectile(projectile);
        return true;
    }
}
