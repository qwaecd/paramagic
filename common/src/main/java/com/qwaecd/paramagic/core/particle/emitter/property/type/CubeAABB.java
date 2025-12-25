package com.qwaecd.paramagic.core.particle.emitter.property.type;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class CubeAABB implements IDataSerializable {
    private final Vector3f min;
    private final Vector3f max;

    public CubeAABB() {
        this(new Vector3f(), new Vector3f(1.0f));
    }
    public CubeAABB(Vector3f v) {
        this(new Vector3f(0.0f), v);
    }
    public CubeAABB(Vector3f min, Vector3f max) {
        this.min = new Vector3f(
                Math.min(min.x, max.x),
                Math.min(min.y, max.y),
                Math.min(min.z, max.z)
        );
        this.max = new Vector3f(
                Math.max(min.x, max.x),
                Math.max(min.y, max.y),
                Math.max(min.z, max.z)
        );
    }

    public Vector3fc getMinPos() {
        return this.min;
    }

    public Vector3fc getMaxPos() {
        return this.max;
    }

    /**
     * Calculates the center of the AABB and stores it in the destination vector.
     * @param dest The vector to store the result in.
     * @return The destination vector for chaining.
     */
    @SuppressWarnings("UnusedReturnValue")
    public Vector3f getCenter(Vector3f dest) {
        return dest.set(this.min).add(this.max).mul(0.5f);
    }

    public void moveCenterTo(Vector3f newCenter) {
        float offsetX = newCenter.x - (this.min.x + this.max.x) / 2.0f;
        float offsetY = newCenter.y - (this.min.y + this.max.y) / 2.0f;
        float offsetZ = newCenter.z - (this.min.z + this.max.z) / 2.0f;
        this.min.add(offsetX, offsetY, offsetZ);
        this.max.add(offsetX, offsetY, offsetZ);
    }

    public void setAABB(Vector3f minV, Vector3f maxV) {
        this.min.set(
                Math.min(minV.x, maxV.x),
                Math.min(minV.y, maxV.y),
                Math.min(minV.z, maxV.z)
        );
        this.max.set(
                Math.max(minV.x, maxV.x),
                Math.max(minV.y, maxV.y),
                Math.max(minV.z, maxV.z)
        );
    }

    public void setAABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.min.set(
                Math.min(minX, maxX),
                Math.min(minY, maxY),
                Math.min(minZ, maxZ)
        );
        this.max.set(
                Math.max(minX, maxX),
                Math.max(minY, maxY),
                Math.max(minZ, maxZ)
        );
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeVector3f("min", this.min);
        codec.writeVector3f("max", this.max);
    }

    public static CubeAABB fromCodec(DataCodec codec) {
        Vector3f min = codec.readVector3f("min");
        Vector3f max = codec.readVector3f("max");
        return new CubeAABB(min, max);
    }
}
