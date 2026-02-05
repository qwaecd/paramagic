package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.ui.menu.SpellEditTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;


@PlatformScope(PlatformScopeType.CLIENT)
public class SpellEditTableScreen extends MCContainerScreen<SpellEditTableMenu> implements MenuAccess<SpellEditTableMenu> {
    public SpellEditTableScreen(SpellEditTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, new SpellEditTableUI());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    @Override
    public SpellEditTableMenu getMenu() {
        return this.menu;
    }
}
