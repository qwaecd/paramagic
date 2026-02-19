package com.qwaecd.paramagic.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.world.entity.projectile.MagicArrowProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class MagicArrowProjectileRenderer extends ArrowRenderer<MagicArrowProjectile> {
    public static final ResourceLocation NORMAL_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/arrow.png");

    public MagicArrowProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MagicArrowProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    @Nonnull
    public ResourceLocation getTextureLocation(MagicArrowProjectile entity) {
        return NORMAL_ARROW_LOCATION;
    }
}
