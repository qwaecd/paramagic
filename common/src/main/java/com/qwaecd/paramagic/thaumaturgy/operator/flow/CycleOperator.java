package com.qwaecd.paramagic.thaumaturgy.operator.flow;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

/** Marks the current branch lane for restart after it finishes and waits for both delays. */
public final class CycleOperator extends ParaOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("cycle_operator"),
            new ParaOpId.Properties(OperatorType.FLOW, 0.0f, 0.25f, 100)
    );

    public CycleOperator() {
        super(OP_ID, ModItems.CYCLE_OPERATOR);
    }
}
