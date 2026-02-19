package com.qwaecd.paramagic.client.renderer.block;

import com.qwaecd.paramagic.world.block.ModBlockEntityTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;


public final class ModBlockEntityRenderers {

    public static void init(RendererProvider provider) {
        create(provider, ModBlockEntityTypes.SPELL_EDIT_TABLE, SpellEditTableRenderer::new);
    }

    public interface RendererProvider {
        <T extends BlockEntity> void register(BlockEntityType<T> type, RendererFactory<T> renderer);
    }

    public interface RendererFactory<T extends BlockEntity> {
        BlockEntityRenderer<T> get(BlockEntityRendererProvider.Context context);
    }

    public static <T extends BlockEntity> void create(RendererProvider provider, BlockEntityType<T> type, RendererFactory<T> factory) {
        provider.register(type, factory);
    }
}
