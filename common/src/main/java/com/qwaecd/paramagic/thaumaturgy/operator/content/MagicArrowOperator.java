package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.entity.projectile.MagicArrowProjectile;
import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.world.phys.Vec3;

public class MagicArrowOperator extends ProjectileOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(ModRL.inModSpace("magic_arrow_operator"), OperatorType.PROJECTILE);
    public MagicArrowOperator() {
        super(OP_ID, ModItems.MAGIC_ARROW_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        MagicArrowProjectile projectile = new MagicArrowProjectile(context.level);
        SpellCaster caster = context.caster;

        final float strength = 3.0f;
        Vec3 position = caster.eyePosition();
        Vec3 forwarded = caster.forwardVector();
        projectile.setPosition((float) position.x, (float) position.y - 0.1f, (float) position.z);
        projectile.setVelocity((float) forwarded.x * strength, (float) forwarded.y * strength, (float) forwarded.z * strength);
        projectile.setInaccuracy(1.0f);
        context.addProjectile(projectile);
        return true;
    }

    @Override
    public float getTransmissionDelay() {
        return 0.05f;
    }

    @Override
    public float getCycleCooldown() {
        return 0.1f;
    }
}
