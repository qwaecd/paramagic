package com.qwaecd.paramagic.mixin;

import com.qwaecd.paramagic.network.serializer.AllEntityDataSerializers;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityDataSerializers.class)
public abstract class EntityDataSerializersMixin {
    @Inject(
            method = "<clinit>",
            at = @At(value = "TAIL")
    )
    private static void registerCustomSerializers(CallbackInfo ci) {
        AllEntityDataSerializers.forEachSerializer(EntityDataSerializers::registerSerializer);
    }
}
