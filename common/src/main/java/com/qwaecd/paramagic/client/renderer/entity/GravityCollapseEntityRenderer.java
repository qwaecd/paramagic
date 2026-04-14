package com.qwaecd.paramagic.client.renderer.entity;


import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.tools.TimeProvider;
import com.qwaecd.paramagic.world.entity.projectile.GravityCollapseEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class GravityCollapseEntityRenderer extends EmptyEntityRenderer<GravityCollapseEntity> {
    protected GravityCollapseEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(GravityCollapseEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        Vec3 position = entity.getPosition(partialTick);
        entity.setDistortionPosition((float) position.x, (float) position.y, (float) position.z);
        entity.renderEffect(partialTick, TimeProvider.getDeltaTime(Minecraft.getInstance()));
    }
}
