package com.qwaecd.paramagic.thaumaturgy.operator;

import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

public final class RemovedOperatorHandler {
    private RemovedOperatorHandler() {}

    public static void handleRemovedOperators(@Nonnull Level level, @Nonnull List<OperatorMap.Entry> operators) {
        if (operators.isEmpty()) {
            return;
        }
        // Intentionally left blank for now.
        // Future implementations may convert these operators into drops or other compensation.
    }
}
