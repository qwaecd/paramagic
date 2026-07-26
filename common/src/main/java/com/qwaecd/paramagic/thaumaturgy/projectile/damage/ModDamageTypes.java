package com.qwaecd.paramagic.thaumaturgy.projectile.damage;

import com.qwaecd.paramagic.tools.ModRL;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypes {
    public static final ResourceKey<DamageType> ARCANE_PROJECTILE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ModRL.inModSpace("arcane_projectile")
    );
    public static final ResourceKey<DamageType> ARCANE_PROJECTILE_BYPASSES_COOLDOWN = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ModRL.inModSpace("arcane_projectile_bypasses_cooldown")
    );

    private ModDamageTypes() {
    }
}
