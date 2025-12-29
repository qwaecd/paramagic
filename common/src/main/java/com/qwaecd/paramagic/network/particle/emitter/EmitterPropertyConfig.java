package com.qwaecd.paramagic.network.particle.emitter;

import com.qwaecd.paramagic.core.particle.emitter.property.key.PropertyKey;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public final class EmitterPropertyConfig implements IDataSerializable {
    @Nonnull
    public final EmitterPropertyValue<?>[] properties;

    public EmitterPropertyConfig(@Nonnull EmitterPropertyValue<?>[] properties) {
        this.properties = properties;
    }

    public static class Builder {
        private final Set<EmitterPropertyValue<?>> properties;

        public Builder() {
            this.properties = new HashSet<>();
        }

        public <T> Builder addProperty(EmitterPropertyValue<T> property) {
            this.properties.add(property);
            return this;
        }

        public <T> Builder addProperty(PropertyKey<T> propertyKey, T value) {
            this.properties.add(EmitterPropertyValue.of(propertyKey, value));
            return this;
        }

        public EmitterPropertyConfig build() {
            return new EmitterPropertyConfig(this.properties.toArray(EmitterPropertyValue[]::new));
        }
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeObjectArray("properties", this.properties);
    }

    public static EmitterPropertyConfig fromCodec(DataCodec codec) {
        IDataSerializable[] dataArray = codec.readObjectArray("properties", EmitterPropertyValue::fromCodec);
        EmitterPropertyValue<?>[] properties = DataCodec.castObjectArray(dataArray, EmitterPropertyValue[]::new);
        return new EmitterPropertyConfig(properties);
    }
}
