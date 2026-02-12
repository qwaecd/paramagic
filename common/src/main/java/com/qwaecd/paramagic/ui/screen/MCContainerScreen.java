package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.tools.TimeProvider;
import com.qwaecd.paramagic.ui.MCRenderBackend;
import com.qwaecd.paramagic.ui.MenuContent;
import com.qwaecd.paramagic.ui.api.TooltipRenderer;
import com.qwaecd.paramagic.ui.api.UIRenderContext;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

@SuppressWarnings("RedundantMethodOverride")
public abstract class MCContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements MenuAccess<T> {
    protected final UIManager manager;

    public MCContainerScreen(T menu, Inventory playerInventory, Component title, UINode rootNode) {
        super(menu, playerInventory, title);
        TooltipRenderer tooltipRenderer = new TooltipRenderer() {
            @Override
            public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
                MCContainerScreen.this.renderTooltip(guiGraphics, mouseX, mouseY);
            }
            @Override
            public void renderTooltipWithItem(@Nonnull ItemStack itemStack, GuiGraphics guiGraphics, int mouseX, int mouseY) {
                MCContainerScreen.this.renderTooltipWithItem(itemStack, guiGraphics, mouseX, mouseY);
            }
        };
        MenuContent content = new MenuContent(menu, playerInventory);
        this.manager = new UIManager(rootNode, tooltipRenderer, content);
    }

    @Override
    protected void init() {
        super.init();
        this.manager.init();
    }

    @Override
    public void renderTooltip(@Nonnull GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
    }

    protected void renderTooltipWithItem(@Nonnull ItemStack itemStack, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.minecraft == null || this.minecraft.screen == null) {
            return;
        }
        guiGraphics.renderComponentTooltip(this.minecraft.font, Screen.getTooltipFromItem(this.minecraft, itemStack), mouseX, mouseY);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.minecraft == null) {
            return;
        }
        final float deltaTime = TimeProvider.getDeltaTime(this.minecraft);
        UIRenderContext context = new UIRenderContext(
                this.manager, guiGraphics, new MCRenderBackend(guiGraphics, this.font), deltaTime, mouseX, mouseY
        );
        this.manager.render(context);
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
//        super.renderLabels(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.manager.onMouseClick(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // 调用该函数的同时还会调用 mouseMoved() 所以对框架不需要进行处理.
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.manager.onMouseRelease(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.manager.onMouseMove(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.manager.onMouseScroll(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isFocused() {
        return super.isFocused();
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return super.getFocused();
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        super.setFocused(listener);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
    }
}
