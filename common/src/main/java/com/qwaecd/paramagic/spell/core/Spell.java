package com.qwaecd.paramagic.spell.core;

import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.particle.S2CEffectSpawn;
import com.qwaecd.paramagic.network.particle.anchor.AnchorSpec;
import com.qwaecd.paramagic.network.particle.emitter.EmitterConfig;
import com.qwaecd.paramagic.network.particle.emitter.EmitterPropertyConfig;
import com.qwaecd.paramagic.network.particle.emitter.EmitterPropertyValue;
import com.qwaecd.paramagic.particle.EffectSpawnBuilder;
import com.qwaecd.paramagic.particle.server.ServerEffect;
import com.qwaecd.paramagic.particle.server.ServerEffectManager;
import com.qwaecd.paramagic.spell.logic.ExecutionContext;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.SPHERE_RADIUS;

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
        genParticleData(hitResult, casterEntity);
    }

    private void genParticleData(HitResult result, Entity casterEntity) {
        if (!(casterEntity instanceof ServerPlayer serverPlayer)) {
            return;
        }
        Vector3f pos = result.getLocation().toVector3f();
        EffectSpawnBuilder builder = new EffectSpawnBuilder();

        builder
                .setMaxParticles(2_0000)
                .setMaxLifetime(5.0f)
                .setAnchorSpec(AnchorSpec.forStaticPosition(pos));
        {
            ParticleBurst[] bursts = new ParticleBurst[] {
                    new ParticleBurst(0.0f, 1_0000),
                    new ParticleBurst(0.1f, 1_0000)
            };
            EmitterPropertyConfig propConfig = new EmitterPropertyConfig.Builder()
                    .addProperty(AllEmitterProperties.BLOOM_INTENSITY, 3.2f)
                    .addProperty(AllEmitterProperties.LIFE_TIME_RANGE, new Vector2f(0.3f, 3.0f))
                    .addProperty(AllEmitterProperties.VELOCITY_SPREAD, 180.0f)
                    .addProperty(AllEmitterProperties.EMIT_FROM_VOLUME, true)
                    .addProperty(AllEmitterProperties.SPHERE_RADIUS, 5.0f)
                    .addProperty(AllEmitterProperties.VELOCITY_MODE, VelocityModeStates.RADIAL_FROM_CENTER)
                    .addProperty(AllEmitterProperties.BASE_VELOCITY, new Vector3f(5.0f))
                    .build();

            EmitterConfig config = new EmitterConfig(
                    EmitterType.SPHERE,
                    0.0f,
                    pos,
                    propConfig,
                    bursts
            );
            builder.addEmitterConfig(config);
        }
        ServerEffect serverEffect = ServerEffectManager.getInstance().createEffect(builder);
        if (serverEffect == null) {
            return;
        }
        Networking.get().sendToPlayer(serverPlayer, new S2CEffectSpawn(serverEffect.spawnData));
    }
}
