package com.qwaecd.paramagic.spell.builtin.explostion;

import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;
import static com.qwaecd.paramagic.spell.builtin.explostion.ExplosionSpellRuntime.CASTING_TICKS;

public final class ExplosionGPUEffect {
    @Nullable
    private GPUParticleEffect sphereMagicEffect;
    @Nullable
    private GPUParticleEffect aroundEffect;
    @Nullable
    private GPUParticleEffect gatherEffect;
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
        final float distance = ExplosionSpellPresentation.FRONT_LENGTH;
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
        final float distance = ExplosionSpellPresentation.FRONT_LENGTH;
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
            this.ensureGatherEffect(context);
        }
    }

    public void cleanup() {
        if (this.sphereMagicEffect != null) {
            this.sphereMagicEffect.setConsumer(new DeferredCleanupSphere());
        }
        if (this.aroundEffect != null) {
            this.aroundEffect.setConsumer(new DeferredCleanupAround());
        }
        if (this.gatherEffect != null) {
            this.gatherEffect.setConsumer(new GPUParticleEffect.EffectConsumer() {
                private float elapsedTime = 0.0f;
                private boolean flag = false;

                @Override
                public void accept(GPUParticleEffect effect, float deltaTime) {
                    this.elapsedTime += deltaTime;
                    if (!this.flag) {
                        effect.setShouldUpdateEmitter(false);
                        EffectPhysicsParameter parameter = effect.getPhysicsParameter();
                        parameter.setPrimaryForceEnabled(false);
                        parameter.setSecondaryForceParam(-30.2f, -1.0f);
                        parameter.setDragCoefficient(0.1f);
                        parameter.setLinearForceEnabled(false);
                        this.flag = true;
                    }
                    if (this.elapsedTime > 10.0f) {
                        ParticleSystem.getInstance().removeEffect(effect);
                    }
                }
            });
        }
    }

    private void ensureGatherEffect(final ClientSpellContext context) {
        if (this.gatherEffect != null) {
            return;
        }

        List<Emitter> emitters = new ArrayList<>();
        PhysicsParamBuilder builder = new PhysicsParamBuilder();
        builder.primaryForceEnabled(true)
                .primaryForceParam(12.4f, -1.5f)
                .secondaryForceParam(-1.6f, -3.0f)
                .secondaryForceEnabled(true)
                .linearForceEnabled(true)
                .linearForce(0.0f, 0.0f, 0.0f)
                .dragCoefficient(0.8f);
        this.gatherEffect = new GPUParticleEffect(emitters, 10_0000, -1.0f, builder.build());
        this.gatherEffect.setConsumer(new GPUParticleEffect.EffectConsumer() {
            private static final int MAX_EMITTERS = 3;
            private static final float SPAWN_INTERVAL = 1.0f;
            private static final float SPAWN_CHANCE = 0.5f;
            private static final float EMITTER_SPHERE_RADIUS = 8.0f;
            private static final float EMISSION_RADIUS = 0.5f;
            private static final float PARTICLES_PER_SECOND = 500.0f;
            private static final float WIND_LERP_SPEED = 1.5f;

            private final List<GatherEmitterState> activeEmitters = new ArrayList<>();
            private final TransformSample sample = new TransformSample();
            private final Vector3f currentWind = new Vector3f();
            private final Vector3f targetWind = new Vector3f();
            private float spawnTimer = 0.0f;
            private float windTimer = 0.0f;
            private float windDuration = 0.0f;

            @Override
            public void accept(GPUParticleEffect effect, float deltaTime) {
                context.casterSource().applyTo(this.sample);
                Vector3f eyePosition = new Vector3f(this.sample.eyePosition);
                Vector3f forward = new Vector3f(this.sample.forward).normalize(ExplosionSpellPresentation.FRONT_LENGTH);
                Vector3f spawnPosition = eyePosition.add(forward);
                effect.getPhysicsParameter().setCFPos(spawnPosition);
                this.updateWind(effect, deltaTime);

                for (int i = this.activeEmitters.size() - 1; i >= 0; i--) {
                    GatherEmitterState state = this.activeEmitters.get(i);
                    state.elapsedTime += deltaTime;
                    if (state.elapsedTime >= state.lifeTime) {
                        emitters.remove(state.emitter);
                        this.activeEmitters.remove(i);
                    }
                }

                this.spawnTimer += deltaTime;
                if (this.spawnTimer < SPAWN_INTERVAL) {
                    return;
                }
                this.spawnTimer -= SPAWN_INTERVAL;
                if (this.activeEmitters.size() >= MAX_EMITTERS || random.nextFloat() >= SPAWN_CHANCE) {
                    return;
                }

                Vector3f emitterPosition = randomPointOnSphere(new Vector3f(this.sample.position), EMITTER_SPHERE_RADIUS);
                SphereEmitter emitter = new SphereEmitter(emitterPosition, PARTICLES_PER_SECOND);
                Vector4f color = colorPool[Math.abs(random.nextInt() % colorPool.length)];
                emitter.modifyProp(COLOR, v -> v.set(color));
                emitter.modifyProp(LIFE_TIME_RANGE, v -> v.set(4.5f, 15.0f));
                emitter.modifyProp(SIZE_RANGE, v -> v.set(1.02f, 4.06f));
                emitter.trySet(BLOOM_INTENSITY, 1.0f);
                emitter.trySet(SPHERE_RADIUS, EMISSION_RADIUS);
                emitter.trySet(EMIT_FROM_VOLUME, true);
                emitter.trySet(VELOCITY_MODE, VelocityModeStates.RANDOM);
                emitter.trySet(VELOCITY_SPREAD, 180.0f);
                emitter.modifyProp(BASE_VELOCITY, v -> v.set(0.05f));

                emitters.add(emitter);
                this.activeEmitters.add(new GatherEmitterState(emitter, random.nextFloat(2.0f, 5.0f)));
            }

            private void updateWind(GPUParticleEffect effect, float deltaTime) {
                this.windTimer += deltaTime;
                if (this.windTimer >= this.windDuration) {
                    this.windTimer = 0.0f;
                    this.windDuration = random.nextFloat(1.5f, 3.0f);
                    this.targetWind.set(
                            -0.12f + random.nextFloat() * 0.24f,
                            0.02f + random.nextFloat() * 0.06f,
                            -0.12f + random.nextFloat() * 0.24f
                    ).mul(8.0f);
                }
                this.currentWind.lerp(this.targetWind, Math.min(1.0f, deltaTime * WIND_LERP_SPEED));
                effect.setLinearForceWorld(this.currentWind);
            }
        });
        ParticleSystem.getInstance().spawnEffect(this.gatherEffect);
    }

    private Vector3f randomPointOnSphere(Vector3f center, float radius) {
        float z = this.random.nextFloat() * 2.0f - 1.0f;
        float theta = (float) (this.random.nextFloat() * Math.PI * 2.0);
        float xy = (float) Math.sqrt(Math.max(0.0f, 1.0f - z * z));
        return center.add(
                radius * xy * (float) Math.cos(theta),
                radius * z,
                radius * xy * (float) Math.sin(theta)
        );
    }

    private static final class GatherEmitterState {
        private final SphereEmitter emitter;
        private final float lifeTime;
        private float elapsedTime = 0.0f;

        private GatherEmitterState(SphereEmitter emitter, float lifeTime) {
            this.emitter = emitter;
            this.lifeTime = lifeTime;
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
                parameter.setDragCoefficient(0.01f);
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
