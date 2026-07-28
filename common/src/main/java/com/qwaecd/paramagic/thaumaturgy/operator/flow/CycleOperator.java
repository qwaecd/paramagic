package com.qwaecd.paramagic.thaumaturgy.operator.flow;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;

/** Marks the current branch lane for restart after it finishes and waits for both delays. */
public final class CycleOperator extends ParaOperator {
    public CycleOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("cycle_operator"));
    }
}
