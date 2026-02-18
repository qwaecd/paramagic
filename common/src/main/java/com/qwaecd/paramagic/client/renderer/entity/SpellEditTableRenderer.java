package com.qwaecd.paramagic.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.world.block.entity.SpellEditTableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class SpellEditTableRenderer implements BlockEntityRenderer<SpellEditTableBlockEntity> {

    public SpellEditTableRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            SpellEditTableBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight, int packedOverlay
    ) {

    }
}
