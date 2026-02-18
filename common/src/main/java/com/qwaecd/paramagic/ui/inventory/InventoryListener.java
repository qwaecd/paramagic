package com.qwaecd.paramagic.ui.inventory;

import com.qwaecd.paramagic.ui.inventory.slot.UISlot;

public interface InventoryListener {
    void onInventoryChanged(InventoryHolder inventory, UISlot slot);
}
