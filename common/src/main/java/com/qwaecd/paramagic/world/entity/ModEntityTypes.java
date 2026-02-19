package com.qwaecd.paramagic.world.entity;

import com.qwaecd.paramagic.world.entity.projectile.MagicArrowProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;


public final class ModEntityTypes {
    public static EntityType<SpellAnchorEntity> SPELL_ANCHOR_ENTITY;
    public static EntityType<MagicArrowProjectile> MAGIC_ARROW_PROJECTILE;

    private ModEntityTypes() {
    }

    public static void init(EntityTypeProvider provider) {
        SPELL_ANCHOR_ENTITY = create(provider, "spell_anchor", SpellAnchorEntity::new, MobCategory.MISC, builder -> builder.sized(0.1f, 0.1f));
        MAGIC_ARROW_PROJECTILE = create(provider, "magic_arrow_projectile", MagicArrowProjectile::new, MobCategory.MISC, builder -> builder.sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
    }

    public static <T extends Entity> EntityType<T> create(
            EntityTypeProvider provider,
            String name,
            EntityType.EntityFactory<T> factory,
            MobCategory category,
            EntityTypeModifier<T> modifier
    ) {
        return provider.register(name, factory, category, modifier);
    }

    public interface EntityTypeProvider {
        <T extends Entity> EntityType<T> register(
                String name,
                EntityType.EntityFactory<T> factory,
                MobCategory category,
                EntityTypeModifier<T> modifier
        );
    }

    public interface EntityTypeModifier<T extends Entity> {
        EntityType.Builder<T> modify(EntityType.Builder<T> builder);
    }
}
