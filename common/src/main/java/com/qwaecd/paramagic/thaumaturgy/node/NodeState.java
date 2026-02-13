package com.qwaecd.paramagic.thaumaturgy.node;

public enum NodeState {
    /**
     * 节点初始状态
     */
    PENDING,
    /**
     * 当前游标正在其子节点中游走，或者正在处理自身逻辑
     */
    EVALUATING,
    /**
     * 节点及其所有子节点均已处理完毕，解释器不再对该节点进行操作
     */
    RESOLVED
}
