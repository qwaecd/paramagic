package com.qwaecd.paramagic.init;

import com.qwaecd.paramagic.client.renderer.ModEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntityRendererFabric {
    public static void registerAll() {
        ModEntityRenderers.RendererProvider provider = new ModEntityRenderers.RendererProvider() {
            @Override
            public <T extends BlockEntity> void register(BlockEntityType<T> type, ModEntityRenderers.RendererFactory<T> renderer) {
                BlockEntityRenderers.register(type, renderer::get);
            }
        };
        ModEntityRenderers.init(provider);
    }
}
