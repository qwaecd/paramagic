package com.qwaecd.paramagic.spell.session.store;

import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
public class SessionDataKey<T> {
    @Getter
    public final Class<T> typeClass;
    @Getter
    public final int id;

    SessionDataKey(Class<T> typeClass, int id) {
        this.typeClass = typeClass;
        this.id = id;
    }

    public static <T> SessionDataKey<T> of(Class<T> typeClass, int id) {
        return new SessionDataKey<>(typeClass, id);
    }
}
