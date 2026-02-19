package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntityTypesFabric {
    public static void registerAll() {
        var provider = new ModEntityTypes.EntityTypeProvider() {

            @Override
            public <T extends Entity> EntityType<T> register(
                    String name,
                    EntityType.EntityFactory<T> factory,
                    MobCategory category,
                    ModEntityTypes.EntityTypeModifier<T> modifier
            ) {
                return Registry.register(
                        BuiltInRegistries.ENTITY_TYPE,
                        ModRL.inModSpace(name),
                        modifier.modify(EntityType.Builder.of(factory, category)).build(name)
                );
            }
        };
        ModEntityTypes.init(provider);
    }
}
