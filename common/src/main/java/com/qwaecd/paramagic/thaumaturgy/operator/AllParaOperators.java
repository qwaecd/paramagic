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
    private static final Map<String, ParaOpId> ID_MAP = new HashMap<>();

    private AllParaOperators() {}

    public static synchronized void registerOperator(ParaOpId opId, @Nonnull Supplier<ParaOperator> factory) {
        ParaOpId id = factory.get().getId();
        if (!id.equals(opId)) {
            throw new IllegalArgumentException("Factory produced operator with id " + id.getId() + ", expected " + opId.getId());
        }
        if (OPERATORS.containsKey(id)) {
            throw new IllegalArgumentException("ParaOperator with id " + id.getId() + " is already registered.");
        }
        ID_MAP.put(id.getId().toString(), id);
        OPERATORS.put(id, factory);
    }

    public static void registerAll() {
        registerOperator(VoidOperator.OP_ID, VoidOperator::new);
        registerOperator(MagicArrowOperator.OP_ID, MagicArrowOperator::new);
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
