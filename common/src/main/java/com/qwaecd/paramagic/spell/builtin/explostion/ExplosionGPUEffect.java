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
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;
import static com.qwaecd.paramagic.spell.builtin.explostion.ExplosionSpellRuntime.CASTING_TICKS;

public final class ExplosionGPUEffect {
    @Nullable
    private GPUParticleEffect sphereMagicEffect;
    @Nullable
    private GPUParticleEffect aroundEffect;
    private final TransformSample sample = new TransformSample();

    private int elapsedTicks = 0;
    private final Random random = new Random();

    private final Vector4f[] colorPool = new Vector4f[]{
            new Vector4f(1.2f, 0.5f, 0.8f, 1.0f),
            new Vector4f(0.8f, 0.5f, 1.2f, 1.0f),
            new Vector4f(0.74f, 0.58f, 0.9f, 1.0f)
    };

    ExplosionGPUEffect() {
    }

    public void onStart(final ClientSpellContext context) {
        CasterTransformSource source = context.casterSource();
        source.applyTo(this.sample);
        Vector3f eyePosition = new Vector3f(this.sample.eyePosition);
        final float distance = 1.5f;
        Vector3f forward = new Vector3f(this.sample.forward).normalize(distance);
        Vector3f spawnPosition = eyePosition.add(forward);
        {
            SphereEmitter sphereEmitter = new SphereEmitter(new Vector3f(spawnPosition), 400.0f);
            sphereEmitter.modifyProp(COLOR, v -> v.set(1.2f, 0.5f, 0.8f, 1.0f));
            sphereEmitter.modifyProp(LIFE_TIME_RANGE, v -> v.set(3.1f, 8.4f));
            sphereEmitter.modifyProp(SIZE_RANGE, v -> v.set(3.0f, 5.3f));
            sphereEmitter.trySet(BLOOM_INTENSITY, 0.4f);
            sphereEmitter.trySet(SPHERE_RADIUS, 6.0f);
            sphereEmitter.trySet(EMIT_FROM_VOLUME, true);
            sphereEmitter.trySet(VELOCITY_MODE, VelocityModeStates.RADIAL_FROM_CENTER);
            sphereEmitter.modifyProp(BASE_VELOCITY, v -> v.set(0.0f));

            PhysicsParamBuilder builder = new PhysicsParamBuilder();
            builder.centerForcePos(spawnPosition)
                    .primaryForceParam(10.4f, -1.2f)
                    .primaryForceEnabled(true)
                    .secondaryForceParam(-0.9f, -3.0f)
                    .secondaryForceEnabled(true)
                    .dragCoefficient(0.8f);
            this.sphereMagicEffect = new GPUParticleEffect(List.of(sphereEmitter), 10_0000, -1.0f, builder.build());
            ParticleSystem.getInstance().spawnEffect(this.sphereMagicEffect);
        }
        {
            SphereEmitter sphereEmitter = new SphereEmitter(new Vector3f(0.0f), 400.0f);
            sphereEmitter.modifyProp(COLOR, v -> v.set(0.8f, 0.5f, 1.2f, 1.0f));
            sphereEmitter.modifyProp(LIFE_TIME_RANGE, v -> v.set(1.1f, 4.4f));
            sphereEmitter.modifyProp(SIZE_RANGE, v -> v.set(0.04f, 0.16f));
            sphereEmitter.trySet(BLOOM_INTENSITY, 0.4f);
            sphereEmitter.trySet(SPHERE_RADIUS, 18.0f);
            sphereEmitter.trySet(EMIT_FROM_VOLUME, true);
            sphereEmitter.trySet(VELOCITY_MODE, VelocityModeStates.CONE);
            sphereEmitter.trySet(VELOCITY_SPREAD, 120.0f);
            sphereEmitter.trySet(PARTICLE_PRIMITIVE_TYPE, ParticlePrimitiveTypeStates.QUAD);
            sphereEmitter.trySet(PARTICLE_SHAPE_FLAGS, ParticleShapeFlags.JITTERED);
            sphereEmitter.modifyProp(BASE_VELOCITY, v -> v.set(0.0f));

            PhysicsParamBuilder builder = new PhysicsParamBuilder();
            builder.centerForcePos(0.0f, 0.0f, 0.0f)
                    .primaryForceEnabled(false)
                    .secondaryForceEnabled(false)
                    .dragCoefficient(0.0f);
            this.aroundEffect = new GPUParticleEffect(List.of(sphereEmitter), 10_0000, -1.0f, builder.build());
            this.aroundEffect.setShouldUpdateEmitter(false);
            this.aroundEffect.getTransform().setPosition(spawnPosition);

            this.aroundEffect.setConsumer(((effect, deltaTime) -> {
                final Vector3f yAxis = new Vector3f(0.0f, 1.0f, 0.0f);
                effect.getTransform()
                        .rotate((float) Math.toRadians(5.0f / 20.0f), yAxis);
            }));
            ParticleSystem.getInstance().spawnEffect(this.aroundEffect);
        }
    }

    public void tick(ClientSpellContext context) {
        if (this.sphereMagicEffect == null) {
            return;
        }
        this.elapsedTicks++;
        CasterTransformSource source = context.casterSource();
        source.applyTo(this.sample);
        Vector3f eyePosition = new Vector3f(this.sample.eyePosition);
        final float distance = 1.5f;
        Vector3f forward = new Vector3f(this.sample.forward).normalize(distance);
        Vector3f spawnPosition = eyePosition.add(forward);
        this.sphereMagicEffect.getPhysicsParameter().setCFPos(spawnPosition);
        this.sphereMagicEffect.forEachEmitter(emitter -> {
            if (this.elapsedTicks > CASTING_TICKS) {
                emitter.modifyProp(SIZE_RANGE, v -> v.set(0.03f, 0.08f));
                emitter.trySet(PARTICLE_PRIMITIVE_TYPE, ParticlePrimitiveTypeStates.TRIANGLE);
                emitter.trySet(PARTICLE_SHAPE_FLAGS, ParticleShapeFlags.JITTERED);
            }
            emitter.moveTo(spawnPosition);
        });
        if (aroundEffect == null) {
            return;
        }

        if (this.elapsedTicks > CASTING_TICKS) {
            this.aroundEffect.setShouldUpdateEmitter(true);
            this.aroundEffect.forEachEmitter(
                    emitter -> emitter.modifyProp(COLOR, v -> v.set(this.colorPool[Math.abs(this.random.nextInt() % this.colorPool.length)]))
            );
        }
    }

    public void cleanup() {
        if (this.sphereMagicEffect != null) {
            this.sphereMagicEffect.setConsumer(new DeferredCleanupSphere());
        }
        if (this.aroundEffect != null) {
            this.aroundEffect.setConsumer(new DeferredCleanupAround());
        }
    }
    static class DeferredCleanupSphere implements GPUParticleEffect.EffectConsumer {
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
    static class DeferredCleanupAround implements GPUParticleEffect.EffectConsumer {
        private float elapsedTime = 0.0f;
        private boolean flag = false;
        @Override
        public void accept(GPUParticleEffect effect, float deltaTime) {
            this.elapsedTime += deltaTime;
            if (!flag) {
                effect.setShouldUpdateEmitter(false);
                EffectPhysicsParameter parameter = effect.getPhysicsParameter();
                parameter.setPrimaryForceEnabled(false);
                parameter.setSecondaryForceEnabled(true);
                parameter.setSecondaryForceParam(-12.2f, -1.0f);
                parameter.setDragCoefficient(0.0f);
                flag = true;
            }
            if (this.elapsedTime > 10.0f) {
                ParticleSystem.getInstance().removeEffect(effect);
            }
        }
    }
}
