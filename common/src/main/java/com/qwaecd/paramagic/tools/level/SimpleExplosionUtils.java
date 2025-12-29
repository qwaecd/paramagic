package com.qwaecd.paramagic.tools.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;

public final class SimpleExplosionUtils {
    @SuppressWarnings("NullableProblems")
    public static class SimpleCalculator extends ExplosionDamageCalculator {
        public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, FluidState fluid) {
            return state.isAir() && fluid.isEmpty() ? Optional.empty() : Optional.of(Math.max(state.getBlock().getExplosionResistance(), fluid.getExplosionResistance()));
        }

        public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power) {
            return true;
        }
    }
}
