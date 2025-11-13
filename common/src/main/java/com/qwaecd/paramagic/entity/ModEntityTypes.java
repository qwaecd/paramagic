package com.qwaecd.paramagic.entity;

import net.minecraft.world.entity.EntityType;


public class ModEntityTypes {
    public static EntityType<SpellAnchorEntity> SPELL_ANCHOR_ENTITY;

    private ModEntityTypes() {
    }

    @SuppressWarnings("unchecked")
    public static void init(EntityTypeGetter getter) {
        SPELL_ANCHOR_ENTITY = (EntityType<SpellAnchorEntity>) getter.get(SpellAnchorEntity.IDENTIFIER);
    }

    public interface EntityTypeGetter {
        EntityType<?> get(String identify);
    }
}
