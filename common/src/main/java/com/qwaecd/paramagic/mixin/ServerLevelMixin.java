package com.qwaecd.paramagic.mixin;

import com.qwaecd.paramagic.mixinapi.IServerLevel;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements IServerLevel {
    @Unique
    private Consumer<ServerLevel> onLevelTickCallBack$paramagic;

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void onTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        if (this.onLevelTickCallBack$paramagic != null)
            this.onLevelTickCallBack$paramagic.accept((ServerLevel) (Object)this);
    }

    @Override
    public void registerOnLevelTick$paramagic(Consumer<ServerLevel> callback) {
        this.onLevelTickCallBack$paramagic = callback;
    }
}
