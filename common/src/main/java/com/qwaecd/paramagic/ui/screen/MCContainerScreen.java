package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class MCContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements MenuAccess<T> {
    protected final UIManager uiManager;

    public MCContainerScreen(T menu, Inventory playerInventory, Component title, UINode rootNode) {
        super(menu, playerInventory, title);
        this.uiManager = new UIManager(rootNode, this::renderTooltip);
    }

    @Override
    public void renderTooltip(@Nonnull GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.uiManager.onMouseClick(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.uiManager.onMouseRelease(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.uiManager.onMouseMove(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.uiManager.onMouseScroll(mouseX, mouseY, delta)) {
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
