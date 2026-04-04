package com.qwaecd.paramagic.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.world.entity.projectile.LaserProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class LaserProjectileRenderer extends EmptyEntityRenderer<LaserProjectile> {
    public LaserProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(LaserProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        entity.renderBeamEffect(partialTick);
    }
}
