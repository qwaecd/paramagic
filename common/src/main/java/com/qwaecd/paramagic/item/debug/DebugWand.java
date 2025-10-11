package com.qwaecd.paramagic.item.debug;

import com.qwaecd.paramagic.core.particle.ParticleManager;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.impl.*;
import com.qwaecd.paramagic.core.particle.emitter.prop.ParticleBurst;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class DebugWand extends Item {
    private final Random random = new Random();

    public DebugWand(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) {
            spawnTestParticles(level, player);
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    private void spawnTestParticles(Level level, Player player) {
        Vec3 lookAngle = player.getLookAngle();
        Vec3 eyePosition = player.getEyePosition();

        Vector3f emitterPos = new Vector3f(
                (float) eyePosition.x + (float) lookAngle.x * 1.2f,
                (float) eyePosition.y + (float) lookAngle.y * 1.2f,
                (float) eyePosition.z + (float) lookAngle.z * 1.2f
        );
        final float emitterDistance = 10.0f;

        PhysicsParamBuilder physicsParamBuilder = new PhysicsParamBuilder();
        physicsParamBuilder
                .centerForceEnabled(false)
                .centerForceParam(13.6f, -2.0f)
                .centerForcePos(20.0f, 115.0f, 2.0f)
                .centerForceMaxRadius(1000.0f)
                .linearForceEnabled(false)
                .linearForce(0.01f, -0.0981f / 1000.0f, 0.0f)
                .dragCoefficient(0.0f);

        // Line Emitter
        LineEmitter lineEmitter = new LineEmitter(
                new Vector3f(0.0f, 130.0f, 0.0f),
                0.0f
        );
        lineEmitter.startPositionProp.modify(v -> v.set(emitterPos));
        lineEmitter.endPositionProp.modify(v -> v.set(
                emitterPos.x + lookAngle.x * emitterDistance,
                emitterPos.y + lookAngle.y * emitterDistance,
                emitterPos.z + lookAngle.z * emitterDistance)
        );
        lineEmitter.baseVelocityProp.modify(v -> v.set(0.0f, 0.25f, 0.0f));
        lineEmitter.lifetimeRangeProp.modify(v -> v.set(0.1f, 1.3f));
        lineEmitter.colorProp.modify(v -> v.set(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1.0f));
        lineEmitter.bloomIntensityProp.set(random.nextFloat());
        lineEmitter.velocityModeProp.set(VelocityModeStates.RANDOM);
        lineEmitter.addBurst(new ParticleBurst(0.1f, 3000));

        // Sphere Emitter
        SphereEmitter sphereEmitter = new SphereEmitter(
                new Vector3f(),
                0.0f
        );
        sphereEmitter.positionProp.modify(v -> v.set(emitterPos));
        sphereEmitter.sphereRadiusProp.set(random.nextFloat());
        sphereEmitter.baseVelocityProp.modify(v -> v.set(0.0f, 9.3f, 0.0f));
        sphereEmitter.lifetimeRangeProp.modify(v -> v.set(0.1f, 1.4f));
        sphereEmitter.colorProp.modify(v -> v.set(random.nextFloat(),random.nextFloat(),random.nextFloat(), 1.0f));
        sphereEmitter.sizeRangeProp.modify(v -> v.set(1.8f, 3.6f));
        sphereEmitter.bloomIntensityProp.set(random.nextFloat() + 1.0f);
        sphereEmitter.emitFromVolumeProp.set(true);
        sphereEmitter.velocitySpreadProp.set(1.0f);
        sphereEmitter.velocityModeProp.set(VelocityModeStates.RADIAL_FROM_CENTER);
        int numBursts = 1;
        for (int i = 0; i < numBursts; i ++) {
            sphereEmitter.addBurst(new ParticleBurst(0.05f * i + 0.1f, 2000));
        }


        // effect
        GPUParticleEffect effect = new GPUParticleEffect(
                List.of(sphereEmitter, lineEmitter),
                100_0000,
                3.0f,
                physicsParamBuilder.build()
        );

        ParticleManager.getInstance().spawnEffect(effect);
    }
}
