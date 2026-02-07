package com.qwaecd.paramagic.ui.api.event;

import com.qwaecd.paramagic.ui.event.UIEvent;

public final class UIEventKey<E extends UIEvent> {
    public final int eventId;
    public final Class<E> eventClass;

    private UIEventKey(int eventId, Class<E> eventClass) {
        this.eventId = eventId;
        this.eventClass = eventClass;
    }

    public static <E extends UIEvent> UIEventKey<E> of(int eventId, Class<E> eventClass) {
        return new UIEventKey<>(eventId, eventClass);
    }
}
