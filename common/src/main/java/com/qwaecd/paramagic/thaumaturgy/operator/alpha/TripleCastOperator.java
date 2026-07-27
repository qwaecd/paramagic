package com.qwaecd.paramagic.thaumaturgy.operator.alpha;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public final class TripleCastOperator extends MultiCastOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("triple_cast_operator"),
            new ParaOpId.Properties(OperatorType.ALPHA, 0.0f, 0.0f, 1)
    );

    public TripleCastOperator() {
        super(OP_ID, ModItems.TRIPLE_CAST_OPERATOR, 2);
    }
}
