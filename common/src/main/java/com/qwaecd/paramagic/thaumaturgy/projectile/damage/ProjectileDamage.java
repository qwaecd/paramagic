package com.qwaecd.paramagic.thaumaturgy.projectile.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

import java.util.Objects;

public record ProjectileDamage(
        ResourceKey<DamageType> type,
        float amount,
        boolean canHitOwner
) {
    private static final String TYPE_TAG = "type";
    private static final String AMOUNT_TAG = "amount";
    private static final String CAN_HIT_OWNER_TAG = "canHitOwner";

    public ProjectileDamage {
        Objects.requireNonNull(type, "type");
        if (!Float.isFinite(amount) || amount < 0.0f) {
            throw new IllegalArgumentException("amount must be finite and non-negative");
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString(TYPE_TAG, this.type.location().toString());
        tag.putFloat(AMOUNT_TAG, this.amount);
        tag.putBoolean(CAN_HIT_OWNER_TAG, this.canHitOwner);
        return tag;
    }

    public static ProjectileDamage load(CompoundTag tag) {
        ResourceLocation typeId = new ResourceLocation(tag.getString(TYPE_TAG));
        return new ProjectileDamage(
                ResourceKey.create(Registries.DAMAGE_TYPE, typeId),
                tag.getFloat(AMOUNT_TAG),
                tag.getBoolean(CAN_HIT_OWNER_TAG)
        );
    }
}
