package com.qwaecd.paramagic.mixin.client;


import com.qwaecd.paramagic.ui.screen.MCContainerScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRenderHead(CallbackInfo ci) {
        if (((Object)this) instanceof MCContainerScreen) {
            // 阻止原本的 render 方法执行，将渲染时机交给框架处理
            ci.cancel();
        }
    }
}
