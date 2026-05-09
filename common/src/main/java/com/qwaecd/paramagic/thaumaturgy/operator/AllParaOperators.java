package com.qwaecd.paramagic.thaumaturgy.operator;

import com.qwaecd.paramagic.thaumaturgy.operator.flow.VoidOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.*;
import com.qwaecd.paramagic.thaumaturgy.operator.projectile.GravityCollapseOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.projectile.LaserOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.projectile.MagicArrowOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class AllParaOperators {
    private static final Map<ParaOpId, Supplier<ParaOperator>> OPERATORS = new HashMap<>();
    private static final Map<String, ParaOpId> ID_MAP = new HashMap<>();

    private AllParaOperators() {}

    public static synchronized void registerOperator(ParaOpId opId, @Nonnull Supplier<ParaOperator> factory) {
        if (OPERATORS.containsKey(opId)) {
            throw new IllegalArgumentException("ParaOperator with id " + opId.getId() + " is already registered.");
        }
        ID_MAP.put(opId.getId().toString(), opId);
        OPERATORS.put(opId, factory);
    }

    public static void registerAll() {
        registerOperator(VoidOperator.OP_ID, VoidOperator::new);
        registerOperator(AccelerateOperator.OP_ID, AccelerateOperator::new);
        registerOperator(GradualAccelerationOperator.OP_ID, GradualAccelerationOperator::new);
        registerOperator(ShortenLifetimeOperator.OP_ID, ShortenLifetimeOperator::new);
        registerOperator(ExtendLifetimeOperator.OP_ID, ExtendLifetimeOperator::new);
        registerOperator(HeavyOperator.OP_ID, HeavyOperator::new);
        registerOperator(LaserOperator.OP_ID, LaserOperator::new);
        registerOperator(MagicArrowOperator.OP_ID, MagicArrowOperator::new);
        registerOperator(ShortTrackingOperator.OP_ID, ShortTrackingOperator::new);
        registerOperator(TrackingOperator.OP_ID, TrackingOperator::new);
        registerOperator(WeightlessOperator.OP_ID, WeightlessOperator::new);
        registerOperator(GravityCollapseOperator.OP_ID, GravityCollapseOperator::new);
    }

    @Nullable
    public static ParaOpId getIdByString(String idStr) {
        return ID_MAP.get(idStr);
    }

    @Nullable
    public static ParaOperator createOperator(ParaOpId id) {
        var factory = OPERATORS.get(id);
        return factory == null ? null : factory.get();
    }

    public static boolean contains(ParaOpId id) {
        return OPERATORS.containsKey(id);
    }
}
