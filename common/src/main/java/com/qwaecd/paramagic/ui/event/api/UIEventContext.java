package com.qwaecd.paramagic.ui.event.api;

import com.qwaecd.paramagic.ui.core.UIManager;
import com.qwaecd.paramagic.ui.event.UIEvent;

import javax.annotation.Nonnull;

public class UIEventContext<E extends UIEvent> {
    @Nonnull
    public final UIManager manager;

    @Nonnull
    public final UIEventKey<E> eventKey;

    @Nonnull
    public final E event;

    private boolean consumed = false;
    private boolean propagationStopped = false;

    public UIEventContext(@Nonnull UIManager manager, @Nonnull UIEventKey<E> eventKey, @Nonnull E event) {
        this.manager = manager;
        this.eventKey = eventKey;
        this.event = event;
    }

    @Nonnull
    public UIEventKey<E> getEventKey() {
        return this.eventKey;
    }

    @Nonnull
    public E getEvent() {
        return this.event;
    }

    @Nonnull
    public UIManager getManager() {
        return this.manager;
    }

    public boolean isConsumed() {
        return this.consumed;
    }

    public void consume() {
        this.consumed = true;
    }

    public void stopPropagation() {
        this.propagationStopped = true;
    }

    public void consumeAndStopPropagation() {
        this.consumed = true;
        this.propagationStopped = true;
    }

    public boolean isPropagationStopped() {
        return this.propagationStopped;
    }
}
