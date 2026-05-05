package com.qwaecd.paramagic.world.item.debug;

import com.qwaecd.paramagic.spell.arcane.ArcaneSpellCaster;
import com.qwaecd.paramagic.spell.caster.PlayerCaster;
import com.qwaecd.paramagic.spell.core.SessionManagers;
import com.qwaecd.paramagic.spell.server.ServerSession;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.tools.nbt.CrystalComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.Random;

public class DebugWand extends Item {
    private final Random random = new Random();

    public DebugWand() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
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

        ArcaneSpellCaster.castOnServer((ServerLevel) level, caster, crystalData);
        player.startUsingItem(usedHand);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        super.releaseUsing(stack, level, livingEntity, timeCharged);
        if (!(livingEntity instanceof Player player)) {
            return;
        }
        if (level instanceof ServerLevel serverLevel) {
            for (ServerSession serverSession : SessionManagers.getForServer(serverLevel).getSessionsByUUID(player.getUUID())) {
                serverSession.release();
            }
        }
    }
}
