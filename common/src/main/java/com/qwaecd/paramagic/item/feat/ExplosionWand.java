package com.qwaecd.paramagic.item.feat;

import com.qwaecd.paramagic.feature.effect.ClientEffectManager;
import com.qwaecd.paramagic.feature.effect.EXPLOSION;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;


public class ExplosionWand extends Item {
    public ExplosionWand(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20 * 60;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (level.isClientSide) {
            Vec3 lookAngle = player.getLookAngle();
            Vec3 eyePosition = player.getEyePosition();
            Vector3f emitterCenter = new Vector3f(
                    (float) eyePosition.x + (float) lookAngle.x * 2.2f,
                    (float) eyePosition.y + (float) lookAngle.y * 2.2f,
                    (float) eyePosition.z + (float) lookAngle.z * 2.2f
            );
            ClientEffectManager.getInstance().addExplosion(player.getUUID(),
                    new EXPLOSION(emitterCenter,
                            eyePosition.toVector3f(),
                            lookAngle.toVector3f()
                    ));
        }
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player && level.isClientSide) {
            effectTick(level, player, remainingUseDuration);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player player && level.isClientSide) {
            ClientEffectManager.getInstance().removeExplosion(player.getUUID());
        }
    }

    private void effectTick(Level level, Player player, int remainingUseDuration) {
        EXPLOSION explosion = ClientEffectManager.getInstance().getExplosion(player.getUUID());
        if (explosion == null || remainingUseDuration <= 0) {
            return;
        }

        Vec3 lookAngle = player.getLookAngle();
        Vec3 eyePosition = player.getEyePosition();
        Vector3f newEmitterCenter = new Vector3f(
                (float) eyePosition.x + (float) lookAngle.x * 2.2f,
                (float) eyePosition.y + (float) lookAngle.y * 2.2f,
                (float) eyePosition.z + (float) lookAngle.z * 2.2f
        );
        explosion.updateProps(
                newEmitterCenter
        );
        explosion.tick(1.0f / 20.0f);
    }
}
