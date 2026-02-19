package com.qwaecd.paramagic.client.renderer.entity;

import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class ModEntityRenderers {

    public static void init(RendererProvider provider) {
        create(provider, ModEntityTypes.SPELL_ANCHOR_ENTITY, SpellAnchorEntityRenderer::new);
        create(provider, ModEntityTypes.MAGIC_ARROW_PROJECTILE, MagicArrowProjectileRenderer::new);
    }

    public interface RendererProvider {
        <T extends Entity> void register(EntityType<T> type, RendererFactory<T> factory);
    }

    public interface RendererFactory<T extends Entity> {
        EntityRenderer<T> get(EntityRendererProvider.Context context);
    }

    public static <T extends Entity> void create(RendererProvider provider, EntityType<T> type, RendererFactory<T> factory) {
        provider.register(type, factory);
    }
}
