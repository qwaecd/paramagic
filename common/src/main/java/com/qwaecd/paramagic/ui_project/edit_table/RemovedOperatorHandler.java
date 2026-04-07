package com.qwaecd.paramagic.ui_project.edit_table;

import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorMap;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

public final class RemovedOperatorHandler {
    private RemovedOperatorHandler() {}

    public static void handleRemovedOperators(
            @Nonnull Level level,
            BlockPos blockPos,
            @Nonnull List<OperatorMap.Entry> operators
    ) {
        if (operators.isEmpty()) {
            return;
        }
        for (var entry : operators) {
            ParaOperator operator = AllParaOperators.createOperator(entry.opId());
            if (operator == null) {
                continue;
            }
            ItemEntity itemEntity = new ItemEntity(level, blockPos.getX(), blockPos.getY() + 1.0d, blockPos.getZ(), operator.createOperatorItem());
            level.addFreshEntity(itemEntity);
        }
    }
}
