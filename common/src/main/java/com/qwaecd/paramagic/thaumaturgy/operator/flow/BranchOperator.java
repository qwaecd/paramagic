package com.qwaecd.paramagic.thaumaturgy.operator.flow;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;

/**
 * 将当前已准备的法术复制为一条独立的执行脉络。
 * 实际的分支创建由 {@code ArcaneProcessor} 处理；本类只标识控制流操作符。
 */
public final class BranchOperator extends ParaOperator {
    public BranchOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("branch_operator"));
    }
}
