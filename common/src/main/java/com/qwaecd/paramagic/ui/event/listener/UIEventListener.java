package com.qwaecd.paramagic.ui.event.listener;

import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.event.UIEvent;

@FunctionalInterface
public interface UIEventListener<E extends UIEvent> {
    void handleEvent(UIEventContext<E> context);
}
