package com.qwaecd.paramagic.core.particle.emitter.property.key;

@SuppressWarnings("LombokGetterMayBeUsed")
public class PropertyKey<T> {
    private final String name;
    private final Class<T> valueType;

    public PropertyKey(String name, Class<T> valueType) {
        this.name = name;
        this.valueType = valueType;
    }

    public String getName() {
        return name;
    }

    public Class<T> getValueType() {
        return valueType;
    }
}
