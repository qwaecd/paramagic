package com.qwaecd.paramagic.thaumaturgy.kinetics.runtime;

public interface ProjectileRuntimeModifier {
    void applyTick(ProjectileRuntimeModifierContext context, ProjectileKineticsAccumulator accumulator);
}
