package com.qwaecd.paramagic.ui.event.api;

import com.qwaecd.paramagic.ui.event.UIEvent;
import com.qwaecd.paramagic.ui.event.impl.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class AllUIEvents {
    private static final Map<Integer, UIEventKey<? extends UIEvent>> EVENTS = new HashMap<>();
    private AllUIEvents() {}

    public static final UIEventKey<MouseClick> MOUSE_CLICK =        register(0, MouseClick.class);
    public static final UIEventKey<MouseRelease> MOUSE_RELEASE =    register(1, MouseRelease.class);
    public static final UIEventKey<DoubleClick> MOUSE_DOUBLE_CLICK =register(2, DoubleClick.class);
    public static final UIEventKey<WheelEvent> WHEEL =              register(3, WheelEvent.class);
    public static final UIEventKey<MouseOver> MOUSE_OVER =          register(4, MouseOver.class);
    public static final UIEventKey<MouseLeave> MOUSE_LEAVE =        register(5, MouseLeave.class);

    private static <E extends UIEvent> UIEventKey<E> register(int eventId, Class<E> eventClass) {
        if (EVENTS.containsKey(eventId)) {
            throw new IllegalArgumentException("Event ID " + eventId + " is already registered.");
        }

        UIEventKey<E> eventKey = UIEventKey.of(eventId, eventClass);
        EVENTS.put(eventId, eventKey);
        return eventKey;
    }

    @Nullable
    public static UIEventKey<? extends UIEvent> getEventKeyById(int eventId) {
        return EVENTS.get(eventId);
    }

    @SuppressWarnings("unchecked")
    public static <E extends UIEvent> UIEventKey<E> get(int id, Class<E> clazz) {
        UIEventKey<? extends UIEvent> eventKey = EVENTS.get(id);
        if (eventKey == null) {
            throw new IllegalArgumentException("No event registered with ID " + id);
        }

        if (!eventKey.eventClass.equals(clazz)) {
            throw new IllegalArgumentException("Event ID " + id + " is not of type " + clazz.getName());
        }

        return (UIEventKey<E>) eventKey;
    }
}
