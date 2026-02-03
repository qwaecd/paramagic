package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.io.mouse.MouseEvent;
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
        this.uiManager = new UIManager(rootNode, this);
    }

    @Override
    public void renderTooltip(@Nonnull GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
    }

    /**
     * Called when a mouse button is clicked within the GUI element.
     * <p>
     *
     * @param mouseX the X coordinate of the mouse.
     * @param mouseY the Y coordinate of the mouse.
     * @param button the button that was clicked.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.uiManager.handleMouseEvent(new MouseEvent.Click(mouseX, mouseY, button))) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Called when the mouse is dragged within the GUI element.
     * <p>
     *
     * @param mouseX the X coordinate of the mouse.
     * @param mouseY the Y coordinate of the mouse.
     * @param button the button that is being dragged.
     * @param dragX  the X distance of the drag.
     * @param dragY  the Y distance of the drag.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // 在调用该函数的同时也会调用 mouseMoved(), 所以不需要处理
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    /**
     * Called when a mouse button is released within the GUI element.
     * <p>
     *
     * @param mouseX the X coordinate of the mouse.
     * @param mouseY the Y coordinate of the mouse.
     * @param button the button that was released.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.uiManager.handleMouseEvent(new MouseEvent.Released(mouseX, mouseY, button))) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * Called when the mouse is moved within the GUI element.
     *
     * @param mouseX the X coordinate of the mouse.
     * @param mouseY the Y coordinate of the mouse.
     */
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.uiManager.mouseMove(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    /**
     * Called when the mouse wheel is scrolled within the GUI element.
     * <p>
     *
     * @param mouseX the X coordinate of the mouse.
     * @param mouseY the Y coordinate of the mouse.
     * @param delta  the scrolling delta.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.uiManager.handleMouseEvent(new MouseEvent.Scrolled(mouseX, mouseY, delta))) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Called when a keyboard key is released within the GUI element.
     * <p>
     *
     * @param keyCode   the key code of the released key.
     * @param scanCode  the scan code of the released key.
     * @param modifiers the keyboard modifiers.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    /**
     * Called when a character is typed within the GUI element.
     * <p>
     *
     * @param codePoint the code point of the typed character.
     * @param modifiers the keyboard modifiers.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
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

    /**
     * Sets the focus state of the GUI element.
     *
     * @param listener the focused GUI element.
     */
    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        super.setFocused(listener);
    }

    /**
     * Sets the focus state of the GUI element.
     *
     * @param focused {@code true} to apply focus, {@code false} to remove focus
     */
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
    }
}
