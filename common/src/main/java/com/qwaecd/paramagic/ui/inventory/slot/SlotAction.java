package com.qwaecd.paramagic.ui.inventory.slot;

public enum SlotAction {
    /**
     * 将槽位中的物品消耗并填充到节点树的指定节点上。
     * extraData: 节点树路径
     */
    FILL_NODE,
    /**
     * 从节点树的指定节点移除物品信息并返还到槽位。
     * extraData: 节点树路径
     */
    REMOVE_FROM_NODE,
    /**
     * 在节点树内部移动节点物品。
     * extraData: 源路径与目标路径（以分隔符分隔）
     */
    MOVE_NODE_ITEM
}
