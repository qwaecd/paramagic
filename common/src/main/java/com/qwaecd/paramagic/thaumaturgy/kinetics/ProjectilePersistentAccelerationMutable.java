package com.qwaecd.paramagic.thaumaturgy.kinetics;

import org.joml.Vector3f;

public interface ProjectilePersistentAccelerationMutable {
    Vector3f getPersistentAcceleration();

    void setPersistentAcceleration(float x, float y, float z);

    default void setPersistentAcceleration(Vector3f v) {
        this.setPersistentAcceleration(v.x, v.y, v.z);
    }

    void addPersistentAcceleration(float x, float y, float z);

    default void addPersistentAcceleration(Vector3f v) {
        this.addPersistentAcceleration(v.x, v.y, v.z);
    }

    void clearPersistentAcceleration();
}
