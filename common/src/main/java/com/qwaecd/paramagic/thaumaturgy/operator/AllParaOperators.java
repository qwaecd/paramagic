package com.qwaecd.paramagic.thaumaturgy.operator;

import com.qwaecd.paramagic.thaumaturgy.operator.impl.VoidOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class AllParaOperators {
    private static final Map<ParaOpId, ParaOperator> OPERATORS = new HashMap<>();

    private AllParaOperators() {}

    public static synchronized void registerOperator(@Nonnull ParaOperator op) {
        ParaOpId id = op.id;
        if (OPERATORS.containsKey(id)) {
            throw new IllegalArgumentException("ParaOperator with id " + id.getId() + " is already registered.");
        }
        OPERATORS.put(id, op);
    }

    public static void registerAll() {
        registerOperator(new VoidOperator());
    }

    @Nullable
    public static ParaOperator getOperator(ParaOpId id) {
        return OPERATORS.get(id);
    }
}
