package com.qwaecd.paramagic.thaumaturgy.kinetics;

public interface ProjectileInaccuracyMutable {
    void setInaccuracy(float inaccuracy);

    /**
     * 在投射物射出时的随机偏转量。
     */
    float getInaccuracy();
}
