package com.qwaecd.paramagic.ui.inventory;

import com.qwaecd.paramagic.ui.inventory.slot.UISlot;
import net.minecraft.world.inventory.ClickType;

import javax.annotation.Nullable;

public interface IContainerScreen {
    void slotClicked(UISlot slot, int mouseButton, ClickType type);

    /**
     * 发送不一定绑定具体槽位的容器点击；QUICK_CRAFT 的开始与结束阶段会传入 null。
     */
    void clickMenuSlot(@Nullable UISlot slot, int mouseButton, ClickType type);
}
