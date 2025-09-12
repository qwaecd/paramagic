package com.qwaecd.paramagic.client.animation;

@FunctionalInterface
public interface PropertyAccessor<T> {
    void setValue(T value);
}
