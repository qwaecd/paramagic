package com.qwaecd.paramagic.thaumaturgy;

import org.joml.Vector3f;

public interface ProjectileEntity {
    void setPosition(float x, float y, float z);
    default void setPosition(Vector3f v) {
        this.setPosition(v.x, v.y, v.z);
    }

    void setVelocity(float x, float y, float z);
    default void setVelocity(Vector3f v) {
        this.setVelocity(v.x, v.y, v.z);
    }

    void shoot();

    void setInaccuracy(float inaccuracy);
}
