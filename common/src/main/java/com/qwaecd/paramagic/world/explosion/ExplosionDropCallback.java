package com.qwaecd.paramagic.world.explosion;

@FunctionalInterface
public interface ExplosionDropCallback {
    void beforeDestroy(ExplosionBlockEvent event);
}
