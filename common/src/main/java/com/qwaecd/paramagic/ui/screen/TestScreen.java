package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.tools.TimeProvider;
import com.qwaecd.paramagic.ui.MCRenderBackend;
import com.qwaecd.paramagic.ui.UIColor;
import com.qwaecd.paramagic.ui.core.Rect;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.core.UIRenderContext;
import com.qwaecd.paramagic.ui.menu.TestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

@PlatformScope(PlatformScopeType.CLIENT)
public class TestScreen extends AbstractContainerScreen<TestMenu> implements MenuAccess<TestMenu> {
    private final UINode rootNode = new UINode();
    private final UIColor backgroundColor = new UIColor(UIColor.fromRGBA4f(1.0f, 0.0f, 1.0f, 0.5f));

    public TestScreen(TestMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }


    @Override
    protected void init() {
        super.init();
        Rect localRect = this.rootNode.localRect;
        localRect.set(10, 40, 140, 200);
        this.rootNode.layout(localRect.x, localRect.y, localRect.w, localRect.h);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.minecraft == null) {
            return;
        }
        final float deltaTime = TimeProvider.getDeltaTime(this.minecraft);
        UIRenderContext context = new UIRenderContext(new MCRenderBackend(guiGraphics), deltaTime, mouseX, mouseY);
        context.drawQuad(this.rootNode.worldRect, this.backgroundColor);
        this.rootNode.renderTree(context);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    @Override
    public TestMenu getMenu() {
        return this.menu;
    }
}
