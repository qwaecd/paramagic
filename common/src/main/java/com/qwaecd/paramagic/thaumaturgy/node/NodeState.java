package com.qwaecd.paramagic.thaumaturgy.node;

public enum NodeState {
    /**
     * 节点初始状态
     */
    PENDING,
    /**
     * 节点已经被存入过 context，但还未访问其子节点
     */
    VISITED,
    /**
     * 当前游标正在其子节点中游走
     */
    EVALUATING,
    /**
     * 节点及其所有子节点均已处理完毕，解释器不再对该节点进行操作
     */
    RESOLVED
}
