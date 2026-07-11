package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.ui.menu.SpellEditMenu;
import com.qwaecd.paramagic.ui_project.wand.SpellEditTableUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;


@PlatformScope(PlatformScopeType.CLIENT)
public class SpellEditTableScreen extends MCContainerScreen<SpellEditMenu> implements MenuAccess<SpellEditMenu> {
    public SpellEditTableScreen(SpellEditMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, new SpellEditTableUI());
        SpellEditTableUI tableUI = (SpellEditTableUI) this.manager.rootNode;
        tableUI.init(this.manager);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    @Override
    public SpellEditMenu getMenu() {
        return this.menu;
    }
}
