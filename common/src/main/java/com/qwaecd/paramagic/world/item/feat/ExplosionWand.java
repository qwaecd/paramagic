package com.qwaecd.paramagic.world.item.feat;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.spell.SpellSpawner;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpell;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpellRegistry;
import com.qwaecd.paramagic.spell.builtin.impl.ExplosionSpell;
import com.qwaecd.paramagic.spell.caster.PlayerCaster;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
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

import java.util.UUID;


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
            BuiltinSpell builtinSpell = BuiltinSpellRegistry.getSpell(ExplosionSpell.SPELL_ID);
            if (builtinSpell == null) {
                Paramagic.LOG.error("Failed to get Built-in Spell: {} from BuiltinSpellRegistry.", ExplosionSpell.SPELL_ID);
                return InteractionResultHolder.fail(itemstack);
            }
            SpellSpawner.spawnOnServer(serverLevel, PlayerCaster.create(player), builtinSpell.create());
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
        UUID casterId = livingEntity.getUUID();
        if (level instanceof ServerLevel serverLevel) {
            ServerSessionManager sm = SessionManagers.getForServer(serverLevel);
            for (ServerSession serverSession : sm.getSessionsByUUID(casterId)) {
                serverSession.interrupt();
            }
        }
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }
}
