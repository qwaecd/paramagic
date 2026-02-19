package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.client.renderer.block.ModBlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntityRendererFabric {
    public static void registerAll() {
        ModBlockEntityRenderers.RendererProvider provider = new ModBlockEntityRenderers.RendererProvider() {
            @Override
            public <T extends BlockEntity> void register(BlockEntityType<T> type, ModBlockEntityRenderers.RendererFactory<T> renderer) {
                BlockEntityRenderers.register(type, renderer::get);
            }
        };
        ModBlockEntityRenderers.init(provider);
    }
}
