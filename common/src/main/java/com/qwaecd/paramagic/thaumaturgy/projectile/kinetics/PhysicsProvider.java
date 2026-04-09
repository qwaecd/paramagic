package com.qwaecd.paramagic.thaumaturgy.projectile.kinetics;

import org.joml.Vector3d;

public interface PhysicsProvider {
    double getMass();
    double getInvMass();

    /**
     * 无视物理属性强制修改速度
     */
    void setVelocity(double x, double y, double z);
    default void setVelocity(Vector3d v) {
        this.setVelocity(v.x, v.y, v.z);
    }
    Vector3d getVelocity(Vector3d dest);
    default Vector3d getVelocity() {
        return this.getVelocity(new Vector3d());
    }

    /**
     * 无视物理属性强制增加速度
     */
    void addVelocity(double x, double y, double z);
    default void pushWithMomentum(double x, double y, double z) {
        double invMass = this.getInvMass();
        if (invMass > 0.0d) {
            this.addVelocity(x * invMass, y * invMass, z * invMass);
        }
    }

    void setGravityScale(double scale);
    double getGravityScale();

    void setDragCoefficient(double coefficient);
    double getDragCoefficient();

    void setMaxSpeed(double maxSpeed);
    double getMaxSpeed();
}
