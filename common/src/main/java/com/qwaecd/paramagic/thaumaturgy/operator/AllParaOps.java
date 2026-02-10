package com.qwaecd.paramagic.thaumaturgy.operator;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class AllParaOps {
    private static final Map<ParaOpId, ParaOperator> OPERATORS = new HashMap<>();

    private AllParaOps() {}

    public static synchronized void registerOperator(ParaOpId id, Supplier<ParaOperator> factory) {
        if (OPERATORS.containsKey(id)) {
            throw new IllegalArgumentException("ParaOperator with id " + id.getId() + " is already registered.");
        }
        OPERATORS.put(id, factory.get());
    }

    @Nullable
    public static ParaOperator getOperator(ParaOpId id) {
        return OPERATORS.get(id);
    }
}
