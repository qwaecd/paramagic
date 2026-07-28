package com.qwaecd.paramagic.thaumaturgy.operator.alpha;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;

public final class DoubleCastOperator extends MultiCastOperator {
    public DoubleCastOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("double_cast_operator"), 1);
    }
}
