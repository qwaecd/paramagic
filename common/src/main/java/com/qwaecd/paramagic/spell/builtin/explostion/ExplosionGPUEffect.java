package com.qwaecd.paramagic.spell.builtin.explostion;

import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.impl.SphereEmitter;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticlePrimitiveTypeStates;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticleShapeFlags;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.core.render.TransformSample;
import com.qwaecd.paramagic.spell.client.ClientSpellContext;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;
import static com.qwaecd.paramagic.spell.builtin.explostion.ExplosionSpellRuntime.CASTING_TICKS;

public final class ExplosionGPUEffect {
    @Nullable
    private GPUParticleEffect effect;
    private final TransformSample sample = new TransformSample();

    private int elapsedTicks = 0;

    ExplosionGPUEffect() {
    }

    public void onStart(ClientSpellContext context) {
        CasterTransformSource source = context.casterSource();
        source.applyTo(this.sample);
        SphereEmitter sphereEmitter = new SphereEmitter(new Vector3f(0.0f), 600.0f);
        sphereEmitter.modifyProp(COLOR, v -> v.set(1.2f, 0.5f, 0.8f, 1.0f));
        sphereEmitter.modifyProp(LIFE_TIME_RANGE, v -> v.set(3.1f, 8.4f));
        sphereEmitter.modifyProp(SIZE_RANGE, v -> v.set(2.0f, 3.3f));
        sphereEmitter.trySet(BLOOM_INTENSITY, 0.4f);
        sphereEmitter.trySet(SPHERE_RADIUS, 6.0f);
        sphereEmitter.trySet(EMIT_FROM_VOLUME, false);
        sphereEmitter.trySet(VELOCITY_MODE, VelocityModeStates.RADIAL_FROM_CENTER);
        sphereEmitter.modifyProp(BASE_VELOCITY, v -> v.set(0.4f));

        PhysicsParamBuilder builder = new PhysicsParamBuilder();
        builder.centerForcePos(0.0f, 0.0f, 0.0f)
                .primaryForceParam(1.4f, 1.2f)
                .primaryForceEnabled(true)
                .secondaryForceParam(-0.06f, -5.0f)
                .secondaryForceEnabled(true)
                .dragCoefficient(1.4f);
        this.effect = new GPUParticleEffect(List.of(sphereEmitter), 10_0000, -1.0f, builder.build());
        ParticleSystem.getInstance().spawnEffect(this.effect);
    }

    public void tick(ClientSpellContext context) {
        if (this.effect == null) {
            return;
        }
        this.elapsedTicks++;
        CasterTransformSource source = context.casterSource();
        source.applyTo(this.sample);
        Vector3f eyePosition = new Vector3f(this.sample.eyePosition);
        final float distance = 1.5f;
        Vector3f forward = new Vector3f(this.sample.forward).normalize(distance);
        Vector3f spawnPosition = eyePosition.add(forward);
        this.effect.getPhysicsParameter().setCFPos(spawnPosition);
        this.effect.forEachEmitter(emitter -> {
            if (this.elapsedTicks > CASTING_TICKS) {
                emitter.modifyProp(SIZE_RANGE, v -> v.set(0.03f, 0.08f));
                emitter.trySet(PARTICLE_PRIMITIVE_TYPE, ParticlePrimitiveTypeStates.TRIANGLE);
                emitter.trySet(PARTICLE_SHAPE_FLAGS, ParticleShapeFlags.JITTERED);
            }
            emitter.moveTo(spawnPosition);
        });
    }

    public void cleanup() {
        if (this.effect == null) {
            return;
        }
        this.effect.setConsumer(new DeferredCleanup());
    }
    static class DeferredCleanup implements GPUParticleEffect.EffectConsumer {
        private float elapsedTime = 0.0f;
        private boolean flag = false;
        @Override
        public void accept(GPUParticleEffect effect, float deltaTime) {
            if (!flag) {
                effect.setShouldUpdateEmitter(false);
                EffectPhysicsParameter parameter = effect.getPhysicsParameter();
                parameter.setPrimaryForceEnabled(false);
                parameter.setSecondaryForceParam(-6.2f, -1.0f);
                parameter.setDragCoefficient(0.4f);
                flag = true;
            }
            this.elapsedTime += deltaTime;
            if (this.elapsedTime > 10.0f) {
                ParticleSystem.getInstance().removeEffect(effect);
            }
        }
    }
}
