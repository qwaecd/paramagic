package com.qwaecd.paramagic.ui_project.wand;

import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.inventory.InventoryHolder;
import com.qwaecd.paramagic.ui.screen.MCScreen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class WandEditScreen extends MCScreen {
    private static final Component TITLE = Component.literal("Wand Edit");

    @Nonnull
    private final InventoryHolder playerInv;
    private final WandEditUI rootNode;

    public WandEditScreen(@Nonnull InventoryHolder playerInv) {
        super(TITLE);
        this.playerInv = playerInv;
        this.rootNode = new WandEditUI();
        this.manager = new UIManager(this.rootNode, super.createTooltipRenderer(), null, this.nativeWidgetHost);
    }
}
