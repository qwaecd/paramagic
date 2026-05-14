package com.qwaecd.paramagic.mixin.client;

import com.qwaecd.paramagic.client.replay.FabricReplayCompat;
import com.qwaecd.paramagic.compat.replay.ReplayCompat;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReplayCompat.class)
public abstract class ReplayCompatMixin {
    @Inject(method = "shouldPauseVisuals", at = @At("RETURN"), cancellable = true)
    private static void paramagic$applyFabricReplayCompat(Minecraft minecraft, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(FabricReplayCompat.shouldPauseVisuals(minecraft, cir.getReturnValue()));
    }

    @Inject(method = "getVisualDeltaTime", at = @At("RETURN"), cancellable = true)
    private static void paramagic$applyFabricReplayDeltaCompat(Minecraft minecraft, float vanillaDeltaTime, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(FabricReplayCompat.getVisualDeltaTime(minecraft, cir.getReturnValue()));
    }
}
