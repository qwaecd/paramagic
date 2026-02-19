package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public class VoidOperator extends ParaOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(ModRL.inModSpace("void_operator"), OperatorType.FLOW);

    public VoidOperator() {
        super(OP_ID, ModItems.VOID_OPERATOR);
    }
}
