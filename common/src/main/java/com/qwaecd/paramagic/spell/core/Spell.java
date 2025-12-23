package com.qwaecd.paramagic.spell.core;

import com.qwaecd.paramagic.spell.logic.ExecutionContext;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;

@SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression", "ClassCanBeRecord"})
public class Spell {
    @Getter
    @Nonnull
    public final SpellDefinition definition;

    public Spell(@Nonnull SpellDefinition definition) {
        this.definition = definition;
    }

    public static Spell create(SpellDefinition definition) {
        return new Spell(definition);
    }

    public void execute(ExecutionContext context) {
        // TODO: hard coded explosion effect for demo
        ServerLevel level = context.level;
        Entity casterEntity = level.getEntity(context.caster.getEntityNetworkId());
        if (casterEntity == null) {
            return;
        }
        HitResult hitResult = casterEntity.pick(16.0D, 0.0f, false);
        level.explode(
                casterEntity,
                hitResult.getLocation().x,
                hitResult.getLocation().y,
                hitResult.getLocation().z,
                4.0f,
                true,
                ServerLevel.ExplosionInteraction.BLOCK
        );
    }
}
