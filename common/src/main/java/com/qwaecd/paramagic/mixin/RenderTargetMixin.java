package com.qwaecd.paramagic.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderTarget.class)
public class RenderTargetMixin {
    @Inject(
            method = "resize",
            at = @At("RETURN")
    )
    private void resizeMixin(int width, int height, boolean clearError, CallbackInfo ci) {
        if ((Object)this == Minecraft.getInstance().getMainRenderTarget()) {
            if (ModRenderSystem.isInitialized()) {
                ModRenderSystem.getInstance().onWindowResize(width, height);
            }
        }
    }
}
