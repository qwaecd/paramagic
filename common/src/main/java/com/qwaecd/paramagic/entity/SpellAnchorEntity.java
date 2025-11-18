package com.qwaecd.paramagic.entity;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.assembler.AssemblyException;
import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.network.serializer.AllEntityDataSerializers;
import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class SpellAnchorEntity extends Entity {
    public static final String IDENTIFIER = "spell_anchor";
    private static final int MAX_LIFETIME_TICKS = 20 * 3; // 3s
    /**
     * 仅应该在服务端, 用于定时清除不存在 spell 的实体.
     */
    private int lifetimeTicks = 0;

    private static final EntityDataAccessor<Optional<Spell>> OPTIONAL_SPELL_DATA = SynchedEntityData.defineId(SpellAnchorEntity.class, AllEntityDataSerializers.OPTIONAL_SPELL);

    @Nullable
    protected SpellStateMachine stateMachine = null;

    public SpellAnchorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.setInvulnerable(true);
    }

    @Override
    public void tick() {
        super.tick();
        //noinspection resource
        if (this.lifetimeTicks > MAX_LIFETIME_TICKS && !this.level().isClientSide()) {
            this.discard();
            return;
        }
        Optional<Spell> optionalSpell = this.entityData.get(OPTIONAL_SPELL_DATA);
        if (optionalSpell.isEmpty()) {
            this.lifetimeTicks++;
            return;
        }
        if (this.stateMachine == null) {
            this.lifetimeTicks++;
            return;
        }

        this.stateMachine.update(1.0f / 20.0f);
        if (this.isSpellCompleted()) {
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OPTIONAL_SPELL_DATA, Optional.empty());
    }

    // 测试用的临时字段
    private MagicCircle tmp;
    @Override
    public void onSyncedDataUpdated(@Nonnull EntityDataAccessor<?> key) {
        if (OPTIONAL_SPELL_DATA.equals(key)) {
            Optional<Spell> optionalSpell = this.entityData.get(OPTIONAL_SPELL_DATA);
            //noinspection resource
            if (!this.level().isClientSide()) {
                return;
            }
            if (this.tmp != null) {
                return;
            }
            if (optionalSpell.isEmpty()) {
                return;
            }
            try {
                MagicCircle circle = ParaComposer.assemble(optionalSpell.get().getSpellAssets());
                MagicCircleManager.getInstance().addCircle(circle);
                circle.transform
                        .setPosition((float) this.position().x(), (float) this.position().y() + 0.01f, (float) this.position().z());
                this.tmp = circle;
            } catch (AssemblyException e) {
                Paramagic.LOG.error("Failed to assemble MagicCircle from ParaData in SpellAnchorEntity.", e);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {

    }

    @SuppressWarnings("RedundantMethodOverride")
    @Override
    public boolean isPushable() {
        return false;
    }

    public void attachSpell(@Nonnull Spell spell) {
        this.stateMachine = new SpellStateMachine(spell.getSpellConfig());
        //noinspection resource
        if (this.level().isClientSide())
            return;
        this.entityData.set(OPTIONAL_SPELL_DATA, Optional.of(spell), true);
    }

    public void postEvent(MachineEvent event) {
        if (this.stateMachine == null || !this.containsSpell()) {
            Paramagic.LOG.error("SpellStateMachine is null, cannot post event.");
            return;
        }
        this.stateMachine.postEvent(event);
    }

    public boolean isSpellCompleted() {
        if (this.stateMachine == null || !this.containsSpell()) {
            return true;
        }
        return this.stateMachine.isCompleted();
    }

    public void interrupt() {
        //noinspection resource
        if (this.level().isClientSide() && this.tmp != null) {
            MagicCircleManager.getInstance().removeCircle(this.tmp);
        }
        if (this.stateMachine == null) {
            this.discard();
            return;
        }
        this.stateMachine.interrupt();
    }

    public void forceInterrupt() {
        //noinspection resource
        if (this.level().isClientSide() && this.tmp != null) {
            MagicCircleManager.getInstance().removeCircle(this.tmp);
        }
        if (this.stateMachine == null) {
            this.discard();
            return;
        }
        this.stateMachine.forceInterrupt();
    }

    public void addListener(ISpellPhaseListener listener) {
        if (this.stateMachine == null) {
            Paramagic.LOG.error("SpellStateMachine is null, cannot add listener.");
            return;
        }
        this.stateMachine.addListener(listener);
    }

    public boolean containsSpell() {
        Optional<Spell> optionalSpell = this.entityData.get(OPTIONAL_SPELL_DATA);
        return optionalSpell.isPresent();
    }
}
