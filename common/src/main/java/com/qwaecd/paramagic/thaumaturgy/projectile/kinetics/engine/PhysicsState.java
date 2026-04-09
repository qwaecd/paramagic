package com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import org.joml.Vector3d;

public final class PhysicsState implements IDataSerializable {
    private static final double MIN_MASS = 1.0e-6d;
    private double mass;
    private final double invMass;
    private final Vector3d velocity = new Vector3d();
    private double gravityScale = 1.0d;
    private double dragCoefficient = 0.0d;

    private double maxSpeed = 64.0d;

    public PhysicsState() {
        this(1.0d);
    }

    public PhysicsState(double mass) {
        this.mass = Math.max(mass, MIN_MASS);
        this.invMass = 1.0d / this.mass;
    }

    public double getMass() {
        return this.mass;
    }

    /**
     * @return {@code 1.0d / this.mass}
     */
    public double getInvMass() {
        return this.invMass;
    }

    public void setVelocity(double x, double y, double z) {
        this.velocity.set(x, y, z);
    }

    public void setVelocity(Vector3d velocity) {
        this.velocity.set(velocity);
    }

    public Vector3d getVelocity(Vector3d dest) {
        return dest.set(this.velocity);
    }

    public Vector3d getVelocity() {
        return new Vector3d(this.velocity.x, this.velocity.y, this.velocity.z);
    }

    public void addVelocity(double x, double y, double z) {
        this.velocity.add(x, y, z);
    }

    public void setGravityScale(double scale) {
        this.gravityScale = scale;
    }

    public double getGravityScale() {
        return this.gravityScale;
    }

    public void setDragCoefficient(double coefficient) {
        this.dragCoefficient = coefficient;
    }

    public double getDragCoefficient() {
        return this.dragCoefficient;
    }

    public double getMaxSpeed() {
        return this.maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = Math.max(maxSpeed, 0.0d);
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeDouble("mass", this.mass);
        codec.writeVector3d("velocity", this.velocity);
        codec.writeDouble("gravityScale", this.gravityScale);
        codec.writeDouble("maxSpeed", this.maxSpeed);
    }

    public static PhysicsState fromCodec(DataCodec codec) {
        double mass = codec.readDouble("mass");
        PhysicsState state = new PhysicsState(mass);
        state.velocity.set(codec.readVector3d("velocity"));
        state.gravityScale = codec.readDouble("gravityScale");
        state.maxSpeed = codec.readDouble("maxSpeed");
        return state;
    }

    public void set(PhysicsState other) {
        this.mass = Math.max(other.mass, MIN_MASS);
        this.velocity.set(other.getVelocity());
        this.gravityScale = other.getGravityScale();
        this.dragCoefficient = other.getDragCoefficient();
        this.maxSpeed = other.getMaxSpeed();
    }
}
