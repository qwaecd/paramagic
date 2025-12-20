package com.qwaecd.paramagic.item.feat;

import com.qwaecd.paramagic.feature.effect.exposion.ExplosionAssets;
import com.qwaecd.paramagic.spell.SpellSpawner;
import com.qwaecd.paramagic.spell.caster.PlayerCaster;
import com.qwaecd.paramagic.spell.config.SpellMetaConfig;
import com.qwaecd.paramagic.spell.builder.SpellDefBuilder;
import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.core.SpellDefinition;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
import com.qwaecd.paramagic.spell.view.position.CirclePositionRule;
import com.qwaecd.paramagic.spell.view.position.PositionRuleType;
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
import org.joml.Vector3f;

import java.util.UUID;


public class ExplosionWand extends Item {
    public ExplosionWand(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return Short.MAX_VALUE * 20;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);

        if (level instanceof ServerLevel serverLevel) {
            Spell spell = Spell.create(definition("explosion"));
            SpellSpawner.spawnOnServer(serverLevel, PlayerCaster.create(player), spell);
        }
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(itemstack);
    }

    @SuppressWarnings("SameParameterValue")
    private static SpellDefinition definition(String spellId) {
        return SpellDefBuilder
                .withSpellId(spellId)
                .withMeta(new SpellMetaConfig())
                .phase(SpellPhaseType.IDLE, 0.1f)
                .phaseWithAssets(SpellPhaseType.CASTING, 3.0f).circleAssets(ExplosionAssets.create())
                    .positionRule(CirclePositionRule.fixedAtCasterFeet)
                    .transformConfig(new Vector3f(1.0f), new Vector3f())
                .endAsset()
                .phaseWithAssets(SpellPhaseType.CHANNELING, -1.0f).circleAssets(ExplosionAssets.create())
                    .positionRule(
                            PositionRuleType.IN_FRONT_OF_CASTER,
                            new Vector3f(2.0f),
                            false,
                            new Vector3f((float) Math.toRadians(0.0f), (float) Math.toRadians(0.0f), (float) Math.toRadians(89.0f))
                    )
                    .transformConfig(new Vector3f(0.3f), new Vector3f())
                .endAsset()
                .phase(SpellPhaseType.COOLDOWN, 0.0f)
                .build();
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
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }
}
