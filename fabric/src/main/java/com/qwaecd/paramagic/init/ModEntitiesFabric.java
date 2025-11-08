package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.entity.SpellAnchorEntity;
import com.qwaecd.paramagic.tools.ModRL;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@SuppressWarnings("SameParameterValue")
public class ModEntitiesFabric {
    public static final EntityType<SpellAnchorEntity> SPELL_ANCHOR_ENTITY =
            register(
                    SpellAnchorEntity.IDENTIFIER, SpellAnchorEntity::new,
                    builder -> {
                        builder.sized(0.2f, 0.2f);
                        return builder;
                    }
            );

    private static <T extends Entity> EntityType<T> register(String identifier, EntityType.EntityFactory<T> factory, EntityTypeModifier<T> modifier) {
        return register(identifier, factory, MobCategory.MISC ,modifier);
    }

    private static <T extends Entity> EntityType<T> register(String identifier, EntityType.EntityFactory<T> factory, MobCategory category, EntityTypeModifier<T> modifier) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ModRL.InModSpace(identifier),
                modifier.modify(EntityType.Builder.of(factory, category)).build(identifier)
        );
    }

    public static void registerAll() {
    }

    public interface EntityTypeModifier<T extends Entity> {
        EntityType.Builder<T> modify(EntityType.Builder<T> builder);
    }
}
