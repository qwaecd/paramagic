package com.qwaecd.paramagic.mixin.client;

import com.qwaecd.paramagic.world.item.WandItem;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
            )
    )
    private boolean skipUseSlowdown(LocalPlayer player) {
        return player.isUsingItem() && !WandItem.shouldSkipSlowdown(player.getUseItem());
    }
}
