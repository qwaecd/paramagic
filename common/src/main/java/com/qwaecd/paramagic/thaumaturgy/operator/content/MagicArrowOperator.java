package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public class MagicArrowOperator extends ProjectileOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(ModRL.inModSpace("magic_arrow_operator"), OperatorType.FLOW);
    public MagicArrowOperator() {
        super(OP_ID, ModItems.MAGIC_ARROW_OPERATOR);
    }
}
