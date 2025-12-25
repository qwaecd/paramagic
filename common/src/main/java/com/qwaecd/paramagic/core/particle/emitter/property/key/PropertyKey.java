package com.qwaecd.paramagic.core.particle.emitter.property.key;

@SuppressWarnings("LombokGetterMayBeUsed")
public class PropertyKey<T> {
    private final String name;
    private final T defaultValue;
    private final Class<T> valueType;

    public PropertyKey(String name, T defaultValue, Class<T> valueType) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.valueType = valueType;
    }

    public String getName() {
        return name;
    }
    public T getDefaultValue() {
        return defaultValue;
    }
    public Class<T> getValueType() {
        return valueType;
    }
}
