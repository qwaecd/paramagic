package com.qwaecd.paramagic.mixin.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.qwaecd.paramagic.client.CameraShake;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Camera mainCamera;

    @Shadow
    @Final
    Minecraft minecraft;

    @Inject(
            method = "renderLevel",
            at = @At(
                    shift = At.Shift.AFTER,
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"
            )
    )
    private void renderLevelMixin(float partialTicks, long finishTimeNano, PoseStack poseStack, CallbackInfo ci) {
        CameraShake.applyShake(this.mainCamera, this.minecraft);
    }
}
