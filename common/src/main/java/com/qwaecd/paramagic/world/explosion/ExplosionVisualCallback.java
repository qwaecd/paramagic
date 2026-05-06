package com.qwaecd.paramagic.world.explosion;

public interface ExplosionVisualCallback {
    default void onExplosionStarted(ExplosionVisualEvent event) {
    }

    default void onExplosionFinished(ExplosionVisualEvent event) {
    }
}
