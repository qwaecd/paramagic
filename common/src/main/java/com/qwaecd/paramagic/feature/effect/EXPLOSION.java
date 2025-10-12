package com.qwaecd.paramagic.feature.effect;

import com.qwaecd.paramagic.core.particle.ParticleManager;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.impl.SphereEmitter;
import com.qwaecd.paramagic.core.particle.emitter.impl.VelocityModeStates;
import com.qwaecd.paramagic.feature.MagicCircle;
import lombok.Getter;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public final class EXPLOSION {
    private final Random random = new Random();
    private final List<GPUParticleEffect> particleEffects = new ArrayList<>();
    @Getter
    private final MagicCircle magicCircle;

    public EXPLOSION(Vector3f emitterCenter) {
        this.magicCircle = new MagicCircle();
        initializeParticleEffects(emitterCenter);
    }

    private void initializeParticleEffects(Vector3f emitterCenter) {
        PhysicsParamBuilder physicsParamBuilder = new PhysicsParamBuilder();
        physicsParamBuilder
                .centerForceEnabled(true)
                .centerForceParam(0.4f, -1.0f)
                .centerForcePos(emitterCenter)
                .centerForceMaxRadius(1000.0f)
                .linearForceEnabled(false)
                .linearForce(0.01f, -0.0981f / 1000.0f, 0.0f)
                .dragCoefficient(1.0f);
        // 在玩家面前的法力汇聚球
        SphereEmitter centerParticleBall = new SphereEmitter(
                emitterCenter,
                1000.0f
        );
        centerParticleBall.sphereRadiusProp.set(0.4f);
        centerParticleBall.baseVelocityProp.modify(v -> v.set(0.6f, 0.0f, 0.0f));
        centerParticleBall.emitFromVolumeProp.set(true);
        centerParticleBall.lifetimeRangeProp.modify(v -> v.set(0.8f, 3.0f));
        centerParticleBall.velocityModeProp.set(VelocityModeStates.RANDOM);
        centerParticleBall.sizeRangeProp.modify(v -> v.set(1.0f, 2.0f));
        centerParticleBall.colorProp.modify(v -> v.set(
                0.9f,
                0.4f,
                0.5f,
                1.0f
        ));
        centerParticleBall.bloomIntensityProp.set(1.4f);
        // 汇聚法力粒子特效
        GPUParticleEffect centerEffect = new GPUParticleEffect(
                List.of(centerParticleBall),
                1_0000,
                3600.0f,
                physicsParamBuilder.build()
        );
        submitEffect(centerEffect, 0);
    }


    public void tick(float deltaTime) {
        for (GPUParticleEffect particleEffect : this.particleEffects) {
            particleEffect.update(deltaTime);
        }
        this.magicCircle.update(deltaTime);
    }

    public void updateProps(Vector3f newEmitterCenter) {
        GPUParticleEffect centerBall = this.particleEffects.get(0);
        centerBall.forEachEmitter(emitter -> emitter.moveTo(newEmitterCenter));
        centerBall.getPhysicsParameter().setCFPos(newEmitterCenter);
    }

    public void forEachEffect(Consumer<GPUParticleEffect> consumer) {
        for (GPUParticleEffect effect : this.particleEffects) {
            consumer.accept(effect);
        }
    }

    private void submitEffect(GPUParticleEffect e, int index) {
        this.particleEffects.add(index, e);
    }
}
