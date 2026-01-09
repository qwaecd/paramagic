package com.qwaecd.paramagic.spell.core;

import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.particle.S2CEffectSpawn;
import com.qwaecd.paramagic.network.particle.anchor.AnchorSpec;
import com.qwaecd.paramagic.network.particle.emitter.EmitterConfig;
import com.qwaecd.paramagic.network.particle.emitter.EmitterPropertyConfig;
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
import org.joml.Vector4f;

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
        final float partialTicks = 0.0f;
        final double hitDistance = 24.0D;
        HitResult hitResult = casterEntity.pick(hitDistance, partialTicks, false);
        level.explode(
                casterEntity,
                hitResult.getLocation().x,
                hitResult.getLocation().y,
                hitResult.getLocation().z,
                128.0f,
                false,
                ServerLevel.ExplosionInteraction.BLOCK
        );
        genParticleData(hitResult, casterEntity, level);
    }

    private void genParticleData(HitResult result, Entity casterEntity, ServerLevel level) {
        if (!(casterEntity instanceof ServerPlayer)) {
            return;
        }
        Vector3f pos = result.getLocation().toVector3f();
        EffectSpawnBuilder builder = new EffectSpawnBuilder();
        PhysicsParamBuilder physicsBuilder = new PhysicsParamBuilder();
        physicsBuilder
                .centerForcePos(pos)
                .primaryForceEnabled(true)
                .primaryForceParam(-20.0f, -2.0f)
                .dragCoefficient(0.8f)
                .linearForce(0.0f, -0.98f, 0.0f)
                .linearForceEnabled(true);

        builder
                .setEffectPhysicsParameter(physicsBuilder.build())
                .setMaxParticles(9_0000)
                .setMaxLifetime(5.0f)
                .setAnchorSpec(AnchorSpec.forStaticPosition(pos));
        {
            ParticleBurst[] bursts = new ParticleBurst[] {
                    new ParticleBurst(0.0f, 3_0000)
            };
            EmitterPropertyConfig propConfig = new EmitterPropertyConfig.Builder()
                    .addProperty(AllEmitterProperties.BLOOM_INTENSITY, 1.2f)
                    .addProperty(AllEmitterProperties.LIFE_TIME_RANGE, new Vector2f(0.3f, 5.0f))
                    .addProperty(AllEmitterProperties.SIZE_RANGE, new Vector2f(0.1f, 3.0f))
                    .addProperty(AllEmitterProperties.EMIT_FROM_VOLUME, true)
                    .addProperty(AllEmitterProperties.SPHERE_RADIUS, 1.0f)
                    .addProperty(AllEmitterProperties.VELOCITY_MODE, VelocityModeStates.RANDOM)
                    .addProperty(AllEmitterProperties.BASE_VELOCITY, new Vector3f(0, 2.0f, 0))
                    .addProperty(AllEmitterProperties.POSITION, new Vector3f(pos.x, pos.y + 0.5f, pos.z))
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
        {
            ParticleBurst[] bursts = new ParticleBurst[] {
                    new ParticleBurst(0.0f, 1_5000),
                    new ParticleBurst(0.5f, 1_5000)
            };
            EmitterPropertyConfig propConfig = new EmitterPropertyConfig.Builder()
                    .addProperty(AllEmitterProperties.BLOOM_INTENSITY, 1.2f)
                    .addProperty(AllEmitterProperties.COLOR, new Vector4f(2.0f, 0.5f, 1.0f, 1.0f))
                    .addProperty(AllEmitterProperties.LIFE_TIME_RANGE, new Vector2f(0.3f, 5.0f))
                    .addProperty(AllEmitterProperties.SIZE_RANGE, new Vector2f(0.1f, 3.0f))
                    .addProperty(AllEmitterProperties.VELOCITY_MODE, VelocityModeStates.RANDOM)
                    .addProperty(AllEmitterProperties.BASE_VELOCITY, new Vector3f(0, 2.0f, 0))
                    .addProperty(AllEmitterProperties.POSITION, new Vector3f(pos.x, pos.y - 5.0f, pos.z))
                    .addProperty(AllEmitterProperties.END_POSITION, new Vector3f(pos.x, pos.y + 5.0f, pos.z))
                    .build();

            EmitterConfig config = new EmitterConfig(
                    EmitterType.LINE,
                    0.0f,
                    pos,
                    propConfig,
                    bursts
            );
            builder.addEmitterConfig(config);
        }
        {
            ParticleBurst[] bursts = new ParticleBurst[] {
                    new ParticleBurst(0.0f, 3_0000)
            };
            EmitterPropertyConfig propConfig = new EmitterPropertyConfig.Builder()
                    .addProperty(AllEmitterProperties.INNER_OUTER_RADIUS, new Vector2f(3.0f, 5.0f))
                    .addProperty(AllEmitterProperties.BLOOM_INTENSITY, 1.2f)
                    .addProperty(AllEmitterProperties.COLOR, new Vector4f(3.0f, 1.5f, 1.4f, 1.0f))
                    .addProperty(AllEmitterProperties.LIFE_TIME_RANGE, new Vector2f(0.3f, 5.0f))
                    .addProperty(AllEmitterProperties.SIZE_RANGE, new Vector2f(0.1f, 2.0f))
                    .addProperty(AllEmitterProperties.VELOCITY_MODE, VelocityModeStates.RADIAL_FROM_CENTER)
                    .addProperty(AllEmitterProperties.BASE_VELOCITY, new Vector3f(0, 20.0f, 0))
                    .build();

            EmitterConfig config = new EmitterConfig(
                    EmitterType.CIRCLE,
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

        final double distance = 64.0D;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(result.getLocation()) < distance * distance) {
                Networking.get().sendToPlayer(player, new S2CEffectSpawn(serverEffect.spawnData));
            }
        }
    }
}
