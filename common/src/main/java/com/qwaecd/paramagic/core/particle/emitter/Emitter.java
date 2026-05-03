package com.qwaecd.paramagic.core.particle.emitter;

import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.property.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.property.key.PropertyKey;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface Emitter {
    <T> void setProperty(PropertyKey<T> key, T value);
    @Nullable
    <T> EmitterProperty<T> getProperty(PropertyKey<T> key);

    default <T> void modifyProp(PropertyKey<T> key, Consumer<T> consumer) {
        EmitterProperty<T> prop = this.getProperty(key);
        if (prop != null) {
            consumer.accept(prop.get());
            prop.markDirty();
        }
    }

    default <T> void trySet(PropertyKey<T> key, T value) {
        EmitterProperty<T> prop = this.getProperty(key);
        if (prop != null) {
            prop.set(value);
            prop.markDirty();
        }
    }

    boolean hasProperty(PropertyKey<?> key);

    void moveTo(Vector3f newPos);
    void update(float deltaTime);
    /**
     * @return If particles need to be emitted this frame, returns an EmissionRequest object; otherwise returns null.<br>
     * 如果在这一帧需要发射粒子，则返回一个EmissionRequest对象；否则返回null。
     */
    @Nullable EmissionRequest getEmissionRequest();
    EmitterType getType();
}
