package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.entity.projectile.GravityCollapseEntity;
import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;


public class GravityCollapseOperator extends ProjectileOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("gravity_collapse_operator"),
            new ParaOpId.Properties(OperatorType.PROJECTILE, 2.71828f, 3.14159f)
    );
    public GravityCollapseOperator() {
        super(OP_ID, ModItems.GRAVITY_COLLAPSE_OPERATOR);
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
