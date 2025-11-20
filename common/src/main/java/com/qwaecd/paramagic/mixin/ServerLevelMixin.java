package com.qwaecd.paramagic.mixin;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.mixinapi.IServerLevel;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements IServerLevel {
    @Unique
    private Runnable onLevelTickCallBack$paramagic;

    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    private void unload(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        try {
            if (this.onLevelTickCallBack$paramagic != null)
                this.onLevelTickCallBack$paramagic.run();
        } catch (Exception e) {
            Paramagic.LOG.error("Exception in ServerLevel tick callback", e);
        }
    }

    @Override
    public void registerOnLevelTick$paramagic(Runnable runnable) {
        this.onLevelTickCallBack$paramagic = runnable;
    }
}
