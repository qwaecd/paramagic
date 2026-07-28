package com.qwaecd.paramagic.thaumaturgy.operator.alpha;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;

public final class TripleCastOperator extends MultiCastOperator {
    public TripleCastOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("triple_cast_operator"), 2);
    }
}
