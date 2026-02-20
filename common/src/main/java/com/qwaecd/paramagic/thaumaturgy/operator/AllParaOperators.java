package com.qwaecd.paramagic.thaumaturgy.operator;

import com.qwaecd.paramagic.thaumaturgy.operator.content.MagicArrowOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.content.VoidOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class AllParaOperators {
    private static final Map<ParaOpId, Supplier<ParaOperator>> OPERATORS = new HashMap<>();

    private AllParaOperators() {}

    public static synchronized void registerOperator(@Nonnull Supplier<ParaOperator> factory) {
        ParaOpId id = factory.get().getId();
        if (OPERATORS.containsKey(id)) {
            throw new IllegalArgumentException("ParaOperator with id " + id.getId() + " is already registered.");
        }
        OPERATORS.put(id, factory);
    }

    public static void registerAll() {
        registerOperator(VoidOperator::new);
        registerOperator(MagicArrowOperator::new);
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
