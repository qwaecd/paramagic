package com.qwaecd.paramagic.thaumaturgy.operator.flow;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;

/**
 * 将当前已准备的法术复制为一条独立的执行脉络。
 * 实际的分支创建由 {@code ArcaneProcessor} 处理；本类只标识控制流操作符。
 */
public final class BranchOperator extends ParaOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("branch_operator"),
            new ParaOpId.Properties(OperatorType.FLOW, 0.05f, 0.15f, 20)
    );

    public BranchOperator() {
        super(OP_ID, ModItems.BRANCH_OPERATOR);
    }
}
