package com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine;

import org.joml.Vector3d;

public final class KineticsAccumulator {
    private final Vector3d deltaVelocity = new Vector3d();
    /**
     * Transient acceleration accumulated per tick, unit: blocks/tick^2.
     * With tick-driven integration, this value is applied directly as {@code v += a} each tick.
     */
    private final Vector3d transientAcceleration = new Vector3d();
    private double speedCapOverride = Double.POSITIVE_INFINITY;

    public void reset() {
        this.deltaVelocity.zero();
        this.transientAcceleration.zero();
        this.speedCapOverride = Double.POSITIVE_INFINITY;
    }

    public Vector3d getDeltaVelocity(Vector3d dest) {
        return dest.set(this.deltaVelocity);
    }

    public void addDeltaVelocity(double x, double y, double z) {
        this.deltaVelocity.add(x, y, z);
    }

    public Vector3d getTransientAcceleration(Vector3d dest) {
        return dest.set(this.transientAcceleration);
    }

    /**
     * Adds transient acceleration in blocks/tick^2.
     */
    public void addTransientAcceleration(double x, double y, double z) {
        this.transientAcceleration.add(x, y, z);
    }

    public void addTransientAcceleration(Vector3d value) {
        this.addTransientAcceleration(value.x, value.y, value.z);
    }

    public double getSpeedCapOverride() {
        return this.speedCapOverride;
    }

    public void limitSpeedCap(double speedCap) {
        this.speedCapOverride = Math.min(this.speedCapOverride, speedCap);
    }
}
