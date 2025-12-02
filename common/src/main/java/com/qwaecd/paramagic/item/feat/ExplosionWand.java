package com.qwaecd.paramagic.item.feat;

import com.qwaecd.paramagic.core.accessor.EntityAccessor;
import com.qwaecd.paramagic.entity.SpellAnchorEntity;
import com.qwaecd.paramagic.feature.effect.exposion.EXPLOSION;
import com.qwaecd.paramagic.feature.effect.exposion.ExplosionAssets;
import com.qwaecd.paramagic.mixin.accessor.LevelEntityAccessor;
import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.SpellSpawner;
import com.qwaecd.paramagic.spell.caster.PlayerCaster;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.state.event.AllMachineEvents;
import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
        Vec3 lookAngle = player.getLookAngle();
        Vec3 eyePosition = player.getEyePosition();
        Vector3f emitterCenter = new Vector3f(
                (float) eyePosition.x + (float) lookAngle.x * 2.2f,
                (float) eyePosition.y + (float) lookAngle.y * 2.2f,
                (float) eyePosition.z + (float) lookAngle.z * 2.2f
        );

        EntityAccessor entityAccessor = new EntityAccessor(player);


        if (level.isClientSide()) {
            EXPLOSION explosion = new EXPLOSION(
                    emitterCenter,
                    eyePosition.toVector3f(),
                    lookAngle.toVector3f()
            );
//            spell.addListener(new ExplosionRenderListener(spell, explosion, entityAccessor));
        }
//        spell.addListener(new ExplosionBaseListener(spell, entityAccessor));

//        spell.postEvent(AllMachineEvents.START_CASTING);

//        SpellScheduler.getINSTANCE(level.isClientSide).addSpell(spell);

        if (level instanceof ServerLevel serverLevel) {
            SpellAnchorEntity spellAnchorEntity = new SpellAnchorEntity(level);
            Spell spell = genSpell(spellAnchorEntity.getUUID().toString());

            SpellSession spellSession = SpellSpawner.spawnOnServer(serverLevel, PlayerCaster.create(player), spell);
            if (spellSession != null) {
                itemstack.getOrCreateTagElement("SpellID").putUUID("ID", spellSession.getSessionId());
            }
        }
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(itemstack);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private Spell genSpell(String id) {
        Spell s = new Spell.Builder(id)
                .addPhase(
                        PhaseConfig.create(SpellPhaseType.IDLE, 0.1f)
                )
                .addPhase(
                        PhaseConfig.create(SpellPhaseType.CASTING, 3.0f)
                )
                .addPhase(
                        PhaseConfig.create(SpellPhaseType.CHANNELING, -1.0f)
                )
                .addPhase(
                        PhaseConfig.create(SpellPhaseType.COOLDOWN, 0.0f)
                )
                .build(ExplosionAssets.create());
        return s;
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
        CompoundTag spellIDTag = stack.getTagElement("SpellID");
        if (spellIDTag == null) {
            return;
        }
        UUID uuid = spellIDTag.getUUID("ID");

        LevelEntityAccessor entityAccessor = (LevelEntityAccessor) level;
        Entity entity = entityAccessor.getEntities().get(uuid);
        if (!(entity instanceof SpellAnchorEntity spellAnchorEntity)) {
            return;
        }
//        spellAnchorEntity.interrupt();
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
