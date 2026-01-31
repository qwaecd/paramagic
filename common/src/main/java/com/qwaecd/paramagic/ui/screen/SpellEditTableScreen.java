package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.tools.TimeProvider;
import com.qwaecd.paramagic.ui.MCRenderBackend;
import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import com.qwaecd.paramagic.ui.menu.SpellEditTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

@PlatformScope(PlatformScopeType.CLIENT)
public class SpellEditTableScreen extends MCContainerScreen<SpellEditTableMenu> implements MenuAccess<SpellEditTableMenu> {
    private final UIColor backgroundColor = new UIColor(UIColor.fromRGBA4f(1.0f, 0.0f, 1.0f, 0.5f));

    public SpellEditTableScreen(SpellEditTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, new SpellEditTableUI());
    }

    @Override
    protected void init() {
        super.init();
        this.uiManager.init();
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.minecraft == null) {
            return;
        }
        final float deltaTime = TimeProvider.getDeltaTime(this.minecraft);
        UIRenderContext context = new UIRenderContext(new MCRenderBackend(guiGraphics), deltaTime, mouseX, mouseY);
        this.uiManager.render(context);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    @Override
    public SpellEditTableMenu getMenu() {
        return this.menu;
    }
}
