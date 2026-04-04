package com.qwaecd.paramagic.thaumaturgy;

import org.joml.Vector3f;

public interface ProjectileEntity {
    void setPosition(float x, float y, float z);

    default void setPosition(Vector3f v) {
        this.setPosition(v.x, v.y, v.z);
    }

    /**
     * 将投射物射出到世界中
     */
    void shoot();
}
