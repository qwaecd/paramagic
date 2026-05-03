package com.qwaecd.paramagic.world.item.feat;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.spell.api.AllSpellRuntimes;
import com.qwaecd.paramagic.spell.builtin.AllBuiltinSpellIds;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpellCaster;
import com.qwaecd.paramagic.spell.caster.PlayerCaster;
import com.qwaecd.paramagic.spell.core.SessionManagers;
import com.qwaecd.paramagic.spell.server.ServerSession;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class ExplosionWand extends Item {
    public ExplosionWand() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return Short.MAX_VALUE * 20;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);

        if (level instanceof ServerLevel serverLevel) {
            if (!AllSpellRuntimes.contains(AllBuiltinSpellIds.EXPLOSION)) {
                Paramagic.LOG.error("Failed to get built-in spell runtime: {}", AllBuiltinSpellIds.EXPLOSION);
                return InteractionResultHolder.fail(itemstack);
            }
            BuiltinSpellCaster.castOnServer(serverLevel, PlayerCaster.create(player), AllBuiltinSpellIds.EXPLOSION);
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
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }
        if (level instanceof ServerLevel serverLevel) {
            for (ServerSession serverSession : SessionManagers.getForServer(serverLevel).getSessionsByUUID(player.getUUID())) {
                serverSession.release();
            }
        }
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }
}
