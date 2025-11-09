package com.qwaecd.paramagic.entity;

import com.qwaecd.paramagic.spell.Spell;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpellAnchorEntity extends Entity {
    public static final String IDENTIFIER = "spell_anchor";

    @Nullable
    private Spell spell = null;


    public SpellAnchorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.setInvulnerable(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.spell == null) {
            return;
        }
        this.spell.tick(1.0f / 20.0f);
        if (this.spell.isCompleted()) {
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
    }
}
