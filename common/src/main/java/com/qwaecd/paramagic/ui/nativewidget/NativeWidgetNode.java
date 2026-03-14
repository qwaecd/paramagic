package com.qwaecd.paramagic.ui.nativewidget;

import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.core.UINode;
import com.qwaecd.paramagic.ui.event.impl.DoubleClick;
import com.qwaecd.paramagic.ui.event.impl.MouseClick;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class NativeWidgetNode<W extends GuiEventListener & Renderable & NarratableEntry, N extends NativeWidgetNode<W, N>> extends UINode {
    @Nonnull
    private final NativeWidgetAdapter<N, W> adapter;
    @Nullable
    private W nativeWidget;

    protected NativeWidgetNode(@Nonnull NativeWidgetAdapter<N, W> adapter) {
        this.adapter = adapter;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private N self() {
        return (N) this;
    }

    @Nonnull
    public final NativeWidgetAdapter<N, W> getAdapter() {
        return this.adapter;
    }

    @Nullable
    public final W getNativeWidget() {
        return this.nativeWidget;
    }

    @Nonnull
    public final W createNativeWidget() {
        return this.adapter.createWidget(this.self());
    }

    @Nonnull
    public final W createAndBindNativeWidget() {
        W widget = this.createNativeWidget();
        this.bindNativeWidget(widget);
        return widget;
    }

    public final void bindNativeWidget(@Nonnull W widget) {
        this.nativeWidget = widget;
        this.adapter.syncWidget(this.self(), widget);
    }

    public final void unbindNativeWidget() {
        W widget = this.nativeWidget;
        if (widget == null) {
            return;
        }
        this.adapter.onRemoved(this.self(), widget);
        this.nativeWidget = null;
    }

    public final void syncBoundNativeWidget() {
        W widget = this.nativeWidget;
        if (widget != null) {
            this.adapter.syncWidget(this.self(), widget);
        }
    }

    protected final void syncNativeWidget() {
        UIManager manager = this.getManager();
        if (manager != null) {
            manager.syncNativeWidget(this);
        }
    }

    public final void requestNativeFocus() {
        UIManager manager = this.getManager();
        if (manager != null) {
            manager.focusNativeWidget(this);
        }
    }

    @Override
    protected void onAttached(@Nonnull UIManager manager) {
        manager.bindNativeWidget(this);
    }

    @Override
    protected void onDetached(@Nonnull UIManager manager) {
        manager.unbindNativeWidget(this);
    }

    @Override
    public void layout(float parentX, float parentY, float parentW, float parentH) {
        super.layout(parentX, parentY, parentW, parentH);
        this.syncNativeWidget();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.syncNativeWidget();
    }

    @Override
    public void setHitTestable(boolean hitTestable) {
        super.setHitTestable(hitTestable);
        this.syncNativeWidget();
    }

    @Override
    public void enable() {
        super.enable();
        this.syncNativeWidget();
    }

    @Override
    public void disable() {
        super.disable();
        this.syncNativeWidget();
    }

    @Override
    protected void onMouseClick(UIEventContext<MouseClick> context) {
        context.consume();
        context.getManager().focusNativeWidget(this);
        context.getManager().dispatchNativeWidgetMouseClick(this, context.event.mouseX, context.event.mouseY, context.event.button);
    }

    @Override
    protected void onDoubleClick(UIEventContext<DoubleClick> context) {
        context.consume();
        context.getManager().focusNativeWidget(this);
        context.getManager().dispatchNativeWidgetMouseClick(this, context.event.mouseX, context.event.mouseY, context.event.button);
    }
}
