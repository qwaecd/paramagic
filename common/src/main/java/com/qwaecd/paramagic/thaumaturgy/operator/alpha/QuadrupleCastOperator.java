package com.qwaecd.paramagic.thaumaturgy.operator.alpha;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public final class QuadrupleCastOperator extends MultiCastOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("quadruple_cast_operator"),
            new ParaOpId.Properties(OperatorType.ALPHA, 0.0f, 0.0f, 1)
    );

    public QuadrupleCastOperator() {
        super(OP_ID, ModItems.QUADRUPLE_CAST_OPERATOR, 3);
    }
}
