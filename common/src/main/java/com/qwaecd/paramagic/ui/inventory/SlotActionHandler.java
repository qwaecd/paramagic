package com.qwaecd.paramagic.ui.inventory;

import net.minecraft.server.level.ServerPlayer;

/**
 * 服务端处理自定义槽位操作的接口。
 * <p>
 * Menu 实现此接口来处理来自客户端的 {@link SlotAction} 请求。
 * 具体的操作逻辑（如节点树填充）由各 Menu 子类自行实现。
 */
public interface SlotActionHandler {
    /**
     * 处理客户端发来的自定义槽位操作。
     *
     * @param player    发起操作的玩家
     * @param slotIndex 相关的槽位索引（-1表示无关联槽位）
     * @param action    操作类型
     * @param extraData 额外数据（如节点树路径等，由具体操作定义其格式）
     */
    void handleSlotAction(ServerPlayer player, int slotIndex, SlotAction action, String extraData);
}
