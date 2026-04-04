package com.qwaecd.paramagic.thaumaturgy.kinetics;

import org.joml.Vector3f;

public interface ProjectileVelocityMutable {
    Vector3f getVelocity();

    void setVelocity(float x, float y, float z);

    default void setVelocity(Vector3f v) {
        this.setVelocity(v.x, v.y, v.z);
    }

    /**
     * 直接向当前速度叠加一个增量，其语义等价于冲量。
     */
    void addVelocity(float x, float y, float z);

    default void addVelocity(Vector3f v) {
        this.addVelocity(v.x, v.y, v.z);
    }
}
