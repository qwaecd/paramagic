package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileInaccuracyMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileVelocityMutable;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.entity.projectile.MagicArrowProjectile;
import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MagicArrowOperator extends ProjectileOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("magic_arrow_operator"),
            new ParaOpId.Properties(OperatorType.PROJECTILE, 0.02f, 0.02f)
    );
    public MagicArrowOperator() {
        super(OP_ID, ModItems.MAGIC_ARROW_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        ProjectileEntity projectile = new MagicArrowProjectile(context.level);
        SpellCaster caster = context.caster;
        Entity casterEntity = context.level.getEntity(caster.getEntityNetworkId());

        final float strength = 0.5f;
        Vec3 position = caster.eyePosition();
        Vec3 forwarded = caster.forwardVector();
        projectile.setPosition((float) position.x, (float) position.y - 0.1f, (float) position.z);
        if (casterEntity != null && projectile instanceof net.minecraft.world.entity.projectile.Projectile mcProjectile) {
            mcProjectile.setOwner(casterEntity);
        }
        if (projectile instanceof ProjectileVelocityMutable velocityMutable) {
            velocityMutable.setVelocity((float) forwarded.x * strength, (float) forwarded.y * strength, (float) forwarded.z * strength);
        }
        if (projectile instanceof ProjectileInaccuracyMutable inaccuracyMutable) {
            inaccuracyMutable.setInaccuracy(1.0f);
        }
        context.addProjectile(projectile);
        return true;
    }
}
