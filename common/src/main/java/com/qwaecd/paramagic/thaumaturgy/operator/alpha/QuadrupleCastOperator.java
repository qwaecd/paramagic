package com.qwaecd.paramagic.thaumaturgy.operator.alpha;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;

public final class QuadrupleCastOperator extends MultiCastOperator {
    public QuadrupleCastOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("quadruple_cast_operator"), 3);
    }
}
