package com.qwaecd.paramagic.core.particle.emitter.property.key;

public class PropertyKey<T> {
    private final String name;
    private final T defaultValue;

    public PropertyKey(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }
    public T getDefaultValue() {
        return defaultValue;
    }
}
