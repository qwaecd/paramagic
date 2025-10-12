package com.qwaecd.paramagic.core.particle.emitter;

import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.prop.PropertyKey;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public interface Emitter {
    <T> void setProperty(PropertyKey<T> key, T value);
    <T> EmitterProperty<T> getProperty(PropertyKey<T> key);
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
