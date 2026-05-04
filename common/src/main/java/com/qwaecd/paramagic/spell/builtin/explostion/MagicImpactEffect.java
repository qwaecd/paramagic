package com.qwaecd.paramagic.spell.builtin.explostion;

import com.qwaecd.paramagic.client.material.LaserMaterial;
import com.qwaecd.paramagic.client.obj.laser.LaserBeam;
import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.impl.CircleEmitter;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticleFacingModeStates;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticlePrimitiveTypeStates;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticleShapeFlags;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.api.RenderEffect;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.client.ClientSpellContext;
import com.qwaecd.paramagic.spell.core.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.core.store.SessionDataValue;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.List;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.BASE_VELOCITY;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.BLOOM_INTENSITY;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.INNER_OUTER_RADIUS;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.VELOCITY_MODE;

@PlatformScope(PlatformScopeType.CLIENT)
public final class MagicImpactEffect implements RenderEffect {
    private final float effectTime;
    private float escapedTime = 0;
    private final float maxBeamRadius = 12.0f;
    private final float holdEndBeamRadius = maxBeamRadius - 2.0f;
    @Nonnull
    private final LaserBeam beamOut;
    @Nonnull
    private final LaserBeam beamIn;

    private float currentScale = 0.0f;

    private final GPUParticleEffect explosionParticles;

    public MagicImpactEffect(float effectTime) {
        this.effectTime = effectTime;
        this.beamOut = new LaserBeam(new LaserMaterial(
                LaserMaterial.DEFAULT_FLOW_TEXTURE,
                LaserMaterial.DEFAULT_NOISE_TEXTURE
        )
                .setColor(1.35f, 0.75f, 1.0f)
                .setAlpha(1.0f)
                .setEmissiveIntensity(0.8f)
                .setFlowSpeed(0.01f, -0.9f)
                .setNoiseSpeed(0.01f, -0.9f)
                .setNoiseScale(0.2f)
                .setNoiseStrength(20.5f)
        );
        this.beamIn = new LaserBeam(new LaserMaterial(
                LaserMaterial.DEFAULT_NOISE_TEXTURE,
                LaserMaterial.DEFAULT_NOISE_TEXTURE
        )
                .setColor(0.5f, 0.3f, 0.5f)
                .setAlpha(1.0f)
                .setEmissiveIntensity(2.2f)
                .setFlowSpeed(0.1f, -0.5f)
                .setNoiseSpeed(0.1f, -0.5f)
                .setNoiseScale(0.4f)
                .setNoiseStrength(2.5f)
        );


        CircleEmitter circleEmitter = new CircleEmitter(new Vector3f(0.0f), 1600.0f);
        circleEmitter.modifyProp(COLOR, v -> v.set(1.8f, 0.5f, 0.6f, 1.0f));
        circleEmitter.modifyProp(LIFE_TIME_RANGE, v -> v.set(1.1f, 5.4f));
        circleEmitter.modifyProp(SIZE_RANGE, v -> v.set(0.04f, 0.37f));
        circleEmitter.trySet(BLOOM_INTENSITY, 0.4f);
        circleEmitter.trySet(VELOCITY_MODE, VelocityModeStates.DIRECT);
        circleEmitter.modifyProp(INNER_OUTER_RADIUS, v -> v.set(4.0f, this.maxBeamRadius + 6.0f));
        circleEmitter.modifyProp(BASE_VELOCITY, v -> v.set(0.0f, 24.0f, 0.0f));
        circleEmitter.trySet(PARTICLE_PRIMITIVE_TYPE, ParticlePrimitiveTypeStates.TRIANGLE);
        circleEmitter.trySet(PARTICLE_FACING_MODE, ParticleFacingModeStates.NORMAL_FACING);
        circleEmitter.trySet(PARTICLE_SHAPE_FLAGS, ParticleShapeFlags.JITTERED);
        this.explosionParticles = new GPUParticleEffect(List.of(circleEmitter), 10_0000, this.effectTime + 5.0f);
        this.explosionParticles.setConsumer(((effect, deltaTime) -> {
            if (effect.getCurrentLifeTime() > MagicImpactEffect.this.effectTime) {
                effect.setShouldUpdateEmitter(false);
            }
        }));
    }

    public void spawn(ClientSpellContext context) {
        SessionDataValue<Vector3f> value = context.getDataStore().getValue(AllSessionDataKeys.firstPosition);
        if (value == null) {
            return;
        }
        Vector3f pos = value.getValue();
        this.forEachBeam(beam -> beam.getTransform()
                .setPosition(pos)
                .setScale(0.0f, 1.0f, 0.0f));
        ModRenderSystem.getInstance().spawnRenderEffect(this);
        this.explosionParticles.getTransform().setPosition(pos);
        ParticleSystem.getInstance().spawnEffect(this.explosionParticles);
    }

    interface BeamConsumer {
        void accept(LaserBeam beam);
    }

    private void forEachBeam(BeamConsumer consumer) {
        consumer.accept(this.beamOut);
        consumer.accept(this.beamIn);
    }

    @Override
    public void onAdded(ModRenderSystem renderSystem) {
        ModRenderSystem.getInstance().addRenderable(this.beamOut);
        ModRenderSystem.getInstance().addRenderable(this.beamIn);
    }

    @Override
    public void update(float deltaTime) {
        this.escapedTime += deltaTime;
        this.currentScale = calculateBeamScale(this.escapedTime);
        this.forEachBeam(beam -> beam.getTransform().setScale(this.currentScale, 256.0f, this.currentScale));
    }

    @Override
    public boolean isAlive() {
        return this.escapedTime < this.effectTime;
    }

    @Override
    public void close() {
        ModRenderSystem.getInstance().removeRenderables(this.beamOut, this.beamIn);
    }

    private float calculateBeamScale(float time) {
        if (this.effectTime <= 0) {
            return 0.0f;
        }
        float t1 = this.effectTime * 0.05f;
        float t2 = this.effectTime * 0.85f;

        if (time <= 0.0f) {
            return 0.0f;
        }
        if (time < t1) {
            return lerp(0.0f, this.maxBeamRadius, time / t1);
        }
        if (time < t2) {
            return lerp(this.maxBeamRadius, this.holdEndBeamRadius, (time - t1) / (t2 - t1));
        }
        if (time < this.effectTime) {
            return lerp(this.holdEndBeamRadius, 0.0f, (time - t2) / (this.effectTime - t2));
        }
        return 0.0f;
    }

    private static float lerp(float from, float to, float t) {
        return from + (to - from) * Math.max(0.0f, Math.min(1.0f, t));
    }
}
