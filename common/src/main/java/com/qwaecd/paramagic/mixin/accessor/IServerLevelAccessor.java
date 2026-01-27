package com.qwaecd.paramagic.mixin.accessor;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerLevel.class)
public interface IServerLevelAccessor {
    @Invoker("findLightningTargetAround")
    BlockPos findLightningTargetAroundMethod(BlockPos pos);
}
