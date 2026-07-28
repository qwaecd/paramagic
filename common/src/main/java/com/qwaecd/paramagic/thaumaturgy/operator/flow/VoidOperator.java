package com.qwaecd.paramagic.thaumaturgy.operator.flow;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.ModifierOperator;

public class VoidOperator extends ModifierOperator {
    public VoidOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("void_operator"));
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
    }
}
