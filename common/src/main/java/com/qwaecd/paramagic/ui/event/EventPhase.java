package com.qwaecd.paramagic.ui.event;

public enum EventPhase {
    /**
     * 事件从 root 节点向顶层 node 传播的阶段, 不包括顶层 node 本身.
     */
    CAPTURING,
    /**
     * 事件从顶层 node 向 root 节点传播的阶段, 不包括顶层 node 本身.
     */
    BUBBLING
}
