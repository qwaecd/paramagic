package com.qwaecd.paramagic.thaumaturgy.projectile.kinetics;

import org.joml.Vector3d;

public interface PhysicsProvider {
    double getMass();
    double getInvMass();

    /**
     * 无视物理属性强制修改速度
     * @param syncToClient 是否将修改标记为需要同步至客户端
     */
    void setVelocity(double x, double y, double z, boolean syncToClient);

    default void setVelocity(double x, double y, double z) {
        this.setVelocity(x, y, z, false);
    }

    default void setVelocity(Vector3d v) {
        this.setVelocity(v.x, v.y, v.z);
    }

    Vector3d getVelocity(Vector3d dest);

    default Vector3d getVelocity() {
        return this.getVelocity(new Vector3d());
    }

    /**
     * 无视物理属性强制增加速度
     * @param syncToClient 是否将修改标记为需要同步至客户端
     */
    void addVelocity(double x, double y, double z, boolean syncToClient);
    default void addVelocity(double x, double y, double z) {
        this.addVelocity(x, y, z, false);
    }

    default void pushWithMomentum(double x, double y, double z, boolean syncToClient) {
        double invMass = this.getInvMass();
        if (invMass > 0.0d) {
            this.addVelocity(x * invMass, y * invMass, z * invMass, syncToClient);
        }
    }

    default void pushWithMomentum(double x, double y, double z) {
        this.pushWithMomentum(x, y, z, false);
    }

    void setGravityScale(double scale);
    double getGravityScale();

    void setDragCoefficient(double coefficient);
    double getDragCoefficient();

    void setMaxSpeed(double maxSpeed);
    double getMaxSpeed();
}
