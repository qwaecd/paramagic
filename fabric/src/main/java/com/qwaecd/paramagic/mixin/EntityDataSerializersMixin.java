package com.qwaecd.paramagic.mixin;

import com.qwaecd.paramagic.network.serializer.AllEntityDataSerializers;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityDataSerializers.class)
public abstract class EntityDataSerializersMixin {
    @Shadow
    public static void registerSerializer(EntityDataSerializer<?> serializer) {
    }

    @Inject(
            method = "<clinit>",
            at = @At(value = "TAIL")
    )
    private static void registerCustomSerializers(CallbackInfo ci) {
        registerSerializer(AllEntityDataSerializers.OPTIONAL_SPELL);
    }
}
