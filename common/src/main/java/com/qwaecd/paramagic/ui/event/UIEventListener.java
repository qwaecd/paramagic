package com.qwaecd.paramagic.ui.event;

public interface UIEventListener<E extends UIEvent> {
    void handleEvent(UIEventContext<E> context, E event);
}
