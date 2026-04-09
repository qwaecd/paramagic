package com.qwaecd.paramagic.thaumaturgy.operator.flow;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.ModifierOperator;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

public class VoidOperator extends ModifierOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(ModRL.inModSpace("void_operator"),
            new ParaOpId.Properties(OperatorType.FLOW, -0.025f, 0.02f)
    );

    public VoidOperator() {
        super(OP_ID, ModItems.VOID_OPERATOR);
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
    }
}
