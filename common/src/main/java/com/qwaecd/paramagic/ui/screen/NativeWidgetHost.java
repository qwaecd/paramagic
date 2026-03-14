package com.qwaecd.paramagic.ui.screen;

import com.qwaecd.paramagic.ui.nativewidget.NativeWidgetNode;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public final class NativeWidgetHost {
    @Nonnull
    private final MCContainerScreen<?> screen;
    @Nonnull
    private final Set<NativeWidgetNode<?, ?>> attachedNodes = Collections.newSetFromMap(new IdentityHashMap<>());
    @Nullable
    private NativeWidgetNode<?, ?> focusedNode;

    public NativeWidgetHost(@Nonnull MCContainerScreen<?> screen) {
        this.screen = screen;
    }

    public void bind(@Nonnull NativeWidgetNode<?, ?> node) {
        if (!this.attachedNodes.add(node)) {
            return;
        }

        var widget = node.createAndBindNativeWidget();
        this.screen.addNativeRenderableWidget(widget);
    }

    public void unbind(@Nonnull NativeWidgetNode<?, ?> node) {
        if (!this.attachedNodes.remove(node)) {
            return;
        }

        if (this.focusedNode == node) {
            this.clearFocus();
        }

        GuiEventListener widget = node.getNativeWidget();
        if (widget == null) {
            return;
        }

        this.screen.removeNativeWidget(widget);
        node.unbindNativeWidget();
    }

    public void sync(@Nonnull NativeWidgetNode<?, ?> node) {
        if (!this.attachedNodes.contains(node)) {
            return;
        }
        node.syncBoundNativeWidget();
    }

    public void syncAll() {
        for (NativeWidgetNode<?, ?> node : List.copyOf(this.attachedNodes)) {
            this.sync(node);
        }
    }

    public void focus(@Nonnull NativeWidgetNode<?, ?> node) {
        if (!this.attachedNodes.contains(node)) {
            return;
        }
        if (this.focusedNode == node) {
            return;
        }

        this.clearFocus();
        GuiEventListener widget = node.getNativeWidget();
        if (widget == null) {
            return;
        }

        this.focusedNode = node;
        this.screen.setFocused(widget);
        if (widget instanceof AbstractWidget abstractWidget) {
            abstractWidget.setFocused(true);
        }
    }

    public void clearFocus() {
        if (this.focusedNode == null) {
            return;
        }
        GuiEventListener widget = this.focusedNode.getNativeWidget();
        this.focusedNode = null;
        if (widget == null) {
            return;
        }

        if (this.screen.getFocused() == widget) {
            this.screen.setFocused((GuiEventListener) null);
        }
        if (widget instanceof AbstractWidget abstractWidget) {
            abstractWidget.setFocused(false);
        }
    }

    public boolean dispatchMouseClick(@Nonnull NativeWidgetNode<?, ?> node, double mouseX, double mouseY, int button) {
        this.focus(node);
        GuiEventListener widget = node.getNativeWidget();
        return widget != null && widget.mouseClicked(mouseX, mouseY, button);
    }

    public boolean forwardVanillaMouseClick(double mouseX, double mouseY, int button) {
        return this.screen.forwardVanillaMouseClicked(mouseX, mouseY, button);
    }

    public boolean forwardVanillaMouseRelease(double mouseX, double mouseY, int button) {
        return this.screen.forwardVanillaMouseReleased(mouseX, mouseY, button);
    }
}
