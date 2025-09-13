package com.qwaecd.paramagic.mixin;


import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Timer.class)
public interface TimerMixin {
    @Accessor
    float getMsPerTick();
}
