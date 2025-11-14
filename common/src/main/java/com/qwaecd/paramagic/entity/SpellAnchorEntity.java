package com.qwaecd.paramagic.entity;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpellAnchorEntity extends Entity {
    public static final String IDENTIFIER = "spell_anchor";

    @Nullable
    protected Spell spell = null;
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
        if (this.spell == null || this.stateMachine == null) {
            return;
        }

        this.stateMachine.update(1.0f / 20.0f);
        if (this.isSpellCompleted()) {
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {

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
        this.spell = spell;
        this.stateMachine = new SpellStateMachine(spell.getSpellConfig());
    }

    public void postEvent(MachineEvent event) {
        if (this.stateMachine == null || this.spell == null) {
            Paramagic.LOG.error("SpellStateMachine is null, cannot post event.");
            return;
        }
        this.stateMachine.postEvent(event);
    }

    public boolean isSpellCompleted() {
        if (this.stateMachine == null || this.spell == null) {
            return true;
        }
        return this.stateMachine.isCompleted();
    }

    public void interrupt() {
        if (this.stateMachine == null) {
            this.discard();
            return;
        }
        this.stateMachine.interrupt();
    }

    public void forceInterrupt() {
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
}
