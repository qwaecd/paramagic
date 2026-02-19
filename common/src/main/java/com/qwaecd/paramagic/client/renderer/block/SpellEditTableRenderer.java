package com.qwaecd.paramagic.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.world.block.SpellEditTableBlock;
import com.qwaecd.paramagic.world.block.entity.SpellEditTableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpellEditTableRenderer implements BlockEntityRenderer<SpellEditTableBlockEntity> {
    private final ItemRenderer itemRenderer;

    public SpellEditTableRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            SpellEditTableBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight, int packedOverlay
    ) {
        ItemStack item = blockEntity.getItem(0);
        if (item.isEmpty()) {
            return;
        }

        Direction direction = blockEntity.getBlockState().getValue(SpellEditTableBlock.FACING);

        poseStack.pushPose();
        final float scale = 0.5f;
        poseStack.translate(0.5f, 0.82f, 0.5f);
        switch (direction) {
            case NORTH -> poseStack.mulPose(Direction.SOUTH.getRotation());
            case SOUTH -> poseStack.mulPose(Direction.NORTH.getRotation());
            case WEST -> poseStack.mulPose(Direction.EAST.getRotation());
            case EAST -> poseStack.mulPose(Direction.WEST.getRotation());
        }
        poseStack.translate(0.0f, -0.06f, 0.0f);
        poseStack.scale(scale, scale, scale);
        this.itemRenderer.renderStatic(
                item,
                ItemDisplayContext.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                buffer,
                blockEntity.getLevel(),
                0
        );
        poseStack.popPose();
    }
}
