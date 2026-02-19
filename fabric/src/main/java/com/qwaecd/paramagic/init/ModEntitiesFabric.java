package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import com.qwaecd.paramagic.world.entity.SpellAnchorEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SameParameterValue")
public class ModEntitiesFabric implements ModEntityTypes.EntityTypeGetter {
    public static ModEntitiesFabric instance;
    private static final Map<String, EntityType<?>> REGISTERED_TYPES = new HashMap<>();
    public static final EntityType<SpellAnchorEntity> SPELL_ANCHOR_ENTITY =
            register(
                    SpellAnchorEntity.IDENTIFIER, SpellAnchorEntity::new,
                    builder -> {
                        builder.sized(0.1f, 0.1f);
                        return builder;
                    }
            );

    private static <T extends Entity> EntityType<T> register(String identifier, EntityType.EntityFactory<T> factory, EntityTypeModifier<T> modifier) {
        return register(identifier, factory, MobCategory.MISC ,modifier);
    }

    private static <T extends Entity> EntityType<T> register(String identifier, EntityType.EntityFactory<T> factory, MobCategory category, EntityTypeModifier<T> modifier) {
        EntityType<T> entityType = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ModRL.inModSpace(identifier),
                modifier.modify(EntityType.Builder.of(factory, category)).build(identifier)
        );
        REGISTERED_TYPES.put(identifier, entityType);
        return entityType;
    }

    public static void registerAll() {
        if (instance == null) {
            instance = new ModEntitiesFabric();
        }
    }

    @Override
    public EntityType<?> get(String identify) {
        return REGISTERED_TYPES.get(identify);
    }

    interface EntityTypeModifier<T extends Entity> {
        EntityType.Builder<T> modify(EntityType.Builder<T> builder);
    }
}
