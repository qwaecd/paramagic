package com.qwaecd.paramagic.ui.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PhaseBucket {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhaseBucket.class);
    private List<UIEventListenerEntry<?>> capture = null;
    private List<UIEventListenerEntry<?>> bubble = null;

    public PhaseBucket() {
    }

    @SuppressWarnings("unchecked")
    public <E extends UIEvent> void dispatch(UIEventContext<E> context, EventPhase phase) {
        List<UIEventListenerEntry<?>> listeners = (phase == EventPhase.CAPTURING) ? this.capture : this.bubble;
        if (listeners == null) {
            return;
        }

        for (UIEventListenerEntry<?> entry : listeners) {
            if (context.isPropagationStopped()) {
                break;
            }
            try {
                ((UIEventListenerEntry<E>) entry).handleEvent(context, context.event);
            } catch (Exception e) {
                LOGGER.error("Error while dispatching event in phase {}: {}", phase, entry, e);
            }
        }
    }

    public <E extends UIEvent> UIEventListenerEntry<E> addListener(EventPhase phase, int priority, UIEventListener<E> listener) {
        return switch (phase) {
            case CAPTURING -> this.addCaptureListener(priority, listener);
            case BUBBLING -> this.addBubbleListener(priority, listener);
        };
    }

    private <E extends UIEvent> UIEventListenerEntry<E> addCaptureListener(int priority, UIEventListener<E> listener) {
        if (this.capture == null) {
            this.capture = new ArrayList<>();
        }
        UIEventListenerEntry<E> entry = new UIEventListenerEntry<>(priority, this.capture.size(), listener);
        this.capture.add(entry);
        this.capture.sort(null);
        return entry;
    }

    private <E extends UIEvent> UIEventListenerEntry<E> addBubbleListener(int priority, UIEventListener<E> listener) {
        if (this.bubble == null) {
            this.bubble = new ArrayList<>();
        }
        UIEventListenerEntry<E> entry = new UIEventListenerEntry<>(priority, this.bubble.size(), listener);
        this.bubble.add(entry);
        this.bubble.sort(null);
        return entry;
    }

    public void removeListener(UIEventListenerEntry<?> entry) {
        if (this.capture != null) {
            this.capture.remove(entry);
            if (this.capture.isEmpty()) {
                this.capture = null;
            }
        }
        if (this.bubble != null) {
            this.bubble.remove(entry);
            if (this.bubble.isEmpty()) {
                this.bubble = null;
            }
        }
    }
}
