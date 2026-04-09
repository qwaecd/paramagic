package com.qwaecd.paramagic.thaumaturgy.projectile.property;

public interface LifetimeCarrier {
    /**
     * 获取投射物当前预计存活时间，单位为秒
     */
    float getLifetime();
    void setLifetime(float lifetime);
}
