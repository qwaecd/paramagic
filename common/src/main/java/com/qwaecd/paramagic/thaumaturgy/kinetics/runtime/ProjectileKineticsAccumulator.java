package com.qwaecd.paramagic.thaumaturgy.kinetics.runtime;

import org.joml.Vector3f;

public final class ProjectileKineticsAccumulator {
    private final Vector3f deltaVelocity = new Vector3f();
    private final Vector3f transientAcceleration = new Vector3f();
    private float speedCapOverride = Float.POSITIVE_INFINITY;

    public void clear() {
        this.deltaVelocity.zero();
        this.transientAcceleration.zero();
        this.speedCapOverride = Float.POSITIVE_INFINITY;
    }

    public Vector3f getDeltaVelocity(Vector3f dest) {
        return dest.set(this.deltaVelocity);
    }

    public void addDeltaVelocity(float x, float y, float z) {
        this.deltaVelocity.add(x, y, z);
    }

    public void addDeltaVelocity(Vector3f value) {
        this.addDeltaVelocity(value.x, value.y, value.z);
    }

    public Vector3f getTransientAcceleration(Vector3f dest) {
        return dest.set(this.transientAcceleration);
    }

    public void addTransientAcceleration(float x, float y, float z) {
        this.transientAcceleration.add(x, y, z);
    }

    public void addTransientAcceleration(Vector3f value) {
        this.addTransientAcceleration(value.x, value.y, value.z);
    }

    public float getSpeedCapOverride() {
        return this.speedCapOverride;
    }

    public void limitSpeedCap(float speedCap) {
        this.speedCapOverride = Math.min(this.speedCapOverride, speedCap);
    }
}
