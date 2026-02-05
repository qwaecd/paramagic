package com.qwaecd.paramagic.ui.event.listener;

import com.qwaecd.paramagic.ui.event.UIEvent;
import com.qwaecd.paramagic.ui.event.api.UIEventContext;

@FunctionalInterface
public interface UIEventListener<E extends UIEvent> {
    void handleEvent(UIEventContext<E> context);
}
