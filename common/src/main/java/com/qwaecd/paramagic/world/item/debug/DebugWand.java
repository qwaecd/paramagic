package com.qwaecd.paramagic.world.item.debug;

import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.core.particle.emitter.impl.LineEmitter;
import com.qwaecd.paramagic.core.particle.emitter.impl.SphereEmitter;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.spell.api.SpellSpawner;
import com.qwaecd.paramagic.spell.caster.PlayerCaster;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

public class DebugWand extends Item {
    private final Random random = new Random();

    public DebugWand() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return Short.MAX_VALUE * 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
//        if (level.isClientSide) {
//            this.spawnTestParticles(level, player);
//        }
        if (!level.isClientSide()) {
            this.testArc(level, player, usedHand);
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    private void testArc(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(InteractionHand.OFF_HAND);
        ParaCrystalData crystalData = CrystalComponentUtils.getComponentFromItemStack(itemstack);
        if (crystalData == null) {
            return;
        }
        PlayerCaster caster = PlayerCaster.create(player);

        SpellSpawner.spawnOnServer((ServerLevel) level, caster, crystalData);
        player.startUsingItem(usedHand);
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
                .primaryForceEnabled(false)
                .primaryForceParam(13.6f, -2.0f)
                .centerForcePos(20.0f, 115.0f, 2.0f)
                .primaryForceMaxRadius(1000.0f)
                .linearForceEnabled(false)
                .linearForce(0.01f, -0.0981f / 1000.0f, 0.0f)
                .dragCoefficient(0.0f);

        // Line Emitter
        LineEmitter lineEmitter = new LineEmitter(
                new Vector3f(0.0f, 130.0f, 0.0f),
                0.0f
        );
        lineEmitter.getProperty(POSITION).modify(v -> v.set(emitterPos));
        lineEmitter.getProperty(END_POSITION).modify(v -> v.set(
                emitterPos.x + lookAngle.x * emitterDistance,
                emitterPos.y + lookAngle.y * emitterDistance,
                emitterPos.z + lookAngle.z * emitterDistance)
        );
        lineEmitter.getProperty(BASE_VELOCITY).modify(v -> v.set(0.0f, 0.25f, 0.0f));
        lineEmitter.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(0.1f, 1.3f));
        lineEmitter.getProperty(COLOR).modify(v -> v.set(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1.0f));
        lineEmitter.getProperty(BLOOM_INTENSITY).set(random.nextFloat());
        lineEmitter.getProperty(VELOCITY_MODE).set(VelocityModeStates.RANDOM);
        lineEmitter.addBurst(new ParticleBurst(0.1f, 3000));

        // Sphere Emitter
        SphereEmitter sphereEmitter = new SphereEmitter(
                new Vector3f(),
                0.0f
        );
        sphereEmitter.getProperty(POSITION).modify(v -> v.set(emitterPos));
        sphereEmitter.getProperty(SPHERE_RADIUS).set(random.nextFloat());
        sphereEmitter.getProperty(BASE_VELOCITY).modify(v -> v.set(0.0f, 9.3f, 0.0f));
        sphereEmitter.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(0.1f, 1.4f));
        sphereEmitter.getProperty(COLOR).modify(v -> v.set(random.nextFloat(),random.nextFloat(),random.nextFloat(), 1.0f));
        sphereEmitter.getProperty(SIZE_RANGE).modify(v -> v.set(1.8f, 3.6f));
        sphereEmitter.getProperty(BLOOM_INTENSITY).set(random.nextFloat() + 1.0f);
        sphereEmitter.getProperty(EMIT_FROM_VOLUME).set(true);
        sphereEmitter.getProperty(VELOCITY_SPREAD).set(1.0f);
        sphereEmitter.getProperty(VELOCITY_MODE).set(VelocityModeStates.RADIAL_FROM_CENTER);
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

        ParticleSystem.getInstance().spawnEffect(effect);
    }
}
