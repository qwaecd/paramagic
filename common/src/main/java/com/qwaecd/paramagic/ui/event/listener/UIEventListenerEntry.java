package com.qwaecd.paramagic.ui.event.listener;

import com.qwaecd.paramagic.ui.api.event.UIEventContext;
import com.qwaecd.paramagic.ui.event.UIEvent;

public class UIEventListenerEntry<E extends UIEvent> implements Comparable<UIEventListenerEntry<?>> {
    /**
     * 该监听器的优先级，数值越大优先级越高
     */
    public final int priority;
    /**
     * 监听器在内部 List 的顺序
     */
    final int order;
    private final UIEventListener<E> listener;

    UIEventListenerEntry(int priority, int order, UIEventListener<E> listener) {
        this.priority = priority;
        this.order = order;
        this.listener = listener;
    }

    public void handleEvent(UIEventContext<E> context) {
        this.listener.handleEvent(context);
    }

    @Override
    public int compareTo(UIEventListenerEntry<?> other) {
        if (this.priority != other.priority) {
            return Integer.compare(other.priority, this.priority);
        }
        return Integer.compare(this.order, other.order);
    }
}
