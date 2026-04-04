package com.qwaecd.paramagic.thaumaturgy.kinetics;

import org.joml.Vector3f;

public final class ProjectileKineticsState {
    private final Vector3f velocity = new Vector3f();
    private final Vector3f persistentAcceleration = new Vector3f();
    private float inaccuracy = 0.0f;
    private float linearDamping = 0.0f;
    private float gravityScale = 1.0f;
    private float maxSpeed = 32.0f;

    public Vector3f getVelocity() {
        return new Vector3f(this.velocity);
    }

    public Vector3f getVelocity(Vector3f dest) {
        return dest.set(this.velocity);
    }

    public void setVelocity(float x, float y, float z) {
        this.velocity.set(x, y, z);
    }

    public void addVelocity(float x, float y, float z) {
        this.velocity.add(x, y, z);
    }

    public Vector3f getPersistentAcceleration() {
        return new Vector3f(this.persistentAcceleration);
    }

    public Vector3f getPersistentAcceleration(Vector3f dest) {
        return dest.set(this.persistentAcceleration);
    }

    public void setPersistentAcceleration(float x, float y, float z) {
        this.persistentAcceleration.set(x, y, z);
    }

    public void addPersistentAcceleration(float x, float y, float z) {
        this.persistentAcceleration.add(x, y, z);
    }

    public void clearPersistentAcceleration() {
        this.persistentAcceleration.zero();
    }

    public float getInaccuracy() {
        return this.inaccuracy;
    }

    public void setInaccuracy(float inaccuracy) {
        this.inaccuracy = inaccuracy;
    }

    public float getLinearDamping() {
        return this.linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public float getGravityScale() {
        return this.gravityScale;
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
    }

    public float getMaxSpeed() {
        return this.maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
