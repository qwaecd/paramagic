package com.qwaecd.paramagic.world.explosion;

@FunctionalInterface
public interface ExplosionBlockCallback {
    void beforeDestroy(ExplosionBlockEvent event);
}
