package com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime;

import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.KineticsAccumulator;

public interface ProjectileRuntimeModifier {
    void applyTick(ProjectileRuntimeModifierContext context, KineticsAccumulator accumulator);
}
