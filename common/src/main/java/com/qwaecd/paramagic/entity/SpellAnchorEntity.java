package com.qwaecd.paramagic.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SpellAnchorEntity extends Entity {
    public static final String IDENTIFIER = "spell_anchor";

    public SpellAnchorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.setInvulnerable(true);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @SuppressWarnings("RedundantMethodOverride")
    @Override
    public boolean isPushable() {
        return false;
    }
}
