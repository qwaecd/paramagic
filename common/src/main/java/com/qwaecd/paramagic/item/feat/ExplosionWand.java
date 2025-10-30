package com.qwaecd.paramagic.item.feat;

import com.qwaecd.paramagic.core.accessor.EntityAccessor;
import com.qwaecd.paramagic.feature.effect.exposion.EXPLOSION;
import com.qwaecd.paramagic.feature.effect.exposion.listener.ExplosionBaseListener;
import com.qwaecd.paramagic.feature.effect.exposion.listener.ExplosionRenderListener;
import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.SpellScheduler;
import com.qwaecd.paramagic.spell.state.event.AllMachineEvents;
import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
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

        Spell spell = genSpell(player.getUUID().toString());
        EntityAccessor entityAccessor = new EntityAccessor(player);
        if (level.isClientSide) {
            EXPLOSION explosion = new EXPLOSION(
                    emitterCenter,
                    eyePosition.toVector3f(),
                    lookAngle.toVector3f()
            );
            spell.addListener(new ExplosionRenderListener(spell, explosion, entityAccessor));
        }
        spell.addListener(new ExplosionBaseListener(spell, entityAccessor));

        spell.postEvent(AllMachineEvents.START_CASTING);

        SpellScheduler.getINSTANCE(level.isClientSide).addSpell(spell);

        if (!level.isClientSide) {
            itemstack.getOrCreateTagElement("SpellID").putString("ID", spell.getId());
        }


        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(itemstack);
    }

    private Spell genSpell(String id) {
        Spell s = new Spell.Builder(id)
                .addPhase(
                        PhaseConfig.create(SpellPhaseType.IDLE, 0.1f)
                )
                .addPhase(
                        PhaseConfig.create(SpellPhaseType.CASTING, 3.0f)
                )
                .addPhase(
                        PhaseConfig.create(SpellPhaseType.CHANNELING, 10.0f)
                )
                .addPhase(
                        PhaseConfig.create(SpellPhaseType.COOLDOWN, 0.0f)
                )
                .build();
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
        super.releaseUsing(stack, level, livingEntity, timeCharged);
        CompoundTag spellID = stack.getTagElement("SpellID");
        if (spellID != null) {
            String ID = spellID.getString("ID");
            SpellScheduler.getINSTANCE(level.isClientSide).removeSpell(ID);
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
