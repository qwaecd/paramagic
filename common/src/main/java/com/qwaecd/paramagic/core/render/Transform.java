package com.qwaecd.paramagic.core.render;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@SuppressWarnings("UnusedReturnValue")
public class Transform {
    @Getter
    private final Vector3f position;
    @Getter
    private final Quaternionf rotation;
    @Getter
    private final Vector3f scale;

    private final Matrix4f modelMatrix;
    private boolean isDirty;

    public Transform() {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Quaternionf().identity();
        this.scale = new Vector3f(1, 1, 1);
        this.modelMatrix = new Matrix4f().identity();
        this.isDirty = false;
    }

    /**
     * Sets the position of this transform.<br>
     * 设置此变换的位置。
     *
     * @param x X coordinate<br> X坐标
     * @param y Y coordinate<br> Y坐标
     * @param z Z coordinate<br> Z坐标
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        this.isDirty = true;
        return this;
    }

    /**
     * Sets the position of this transform using a Vector3f.<br>
     * 使用Vector3f设置此变换的位置。
     *
     * @param v The position vector.<br> 位置向量
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform setPosition(Vector3f v) {
        this.position.set(v.x, v.y, v.z);
        this.isDirty = true;
        return this;
    }

    /**
     * Translates the position of this transform by the given offsets.<br>
     * 按指定偏移量平移此变换的位置。
     *
     * @param dx Offset along X axis.<br> X轴偏移量
     * @param dy Offset along Y axis.<br> Y轴偏移量
     * @param dz Offset along Z axis.<br> Z轴偏移量
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform translate(float dx, float dy, float dz) {
        this.position.add(dx, dy, dz);
        this.isDirty = true;
        return this;
    }

    /**
     * Sets the rotation of this transform using an angle and axis.<br>
     * 使用角度和轴设置此变换的旋转。
     *
     * @param angle The rotation angle in radians.<br> 旋转角度（弧度）
     * @param axis The axis to rotate around.<br> 旋转轴
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform setRotation(float angle, Vector3f axis) {
        this.rotation.fromAxisAngleRad(axis, angle);
        this.isDirty = true;
        return this;
    }

    /**
     * Sets the rotation of this transform using a quaternion.<br>
     * 使用四元数设置此变换的旋转。
     *
     * @param quaternion The rotation quaternion.<br> 旋转四元数
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform setRotation(Quaternionf quaternion) {
        this.rotation.set(quaternion);
        this.isDirty = true;
        return this;
    }

    /**
     * Sets the rotation of this transform using Euler angles in degrees.<br>
     * 使用欧拉角（度）设置此变换的旋转。
     *
     * @param x Rotation angle around X axis in degrees.<br> 绕X轴旋转角度（度）
     * @param y Rotation angle around Y axis in degrees.<br> 绕Y轴旋转角度（度）
     * @param z Rotation angle around Z axis in degrees.<br> 绕Z轴旋转角度（度）
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform setRotationDegrees(float x, float y, float z) {
        this.rotation.identity()
                .rotateYXZ((float)Math.toRadians(y), (float)Math.toRadians(x), (float)Math.toRadians(z));
        this.isDirty = true;
        return this;
    }

    /**
     * Sets the rotation of this transform using Euler angles in radians.<br>
     * 使用欧拉角（弧度）设置此变换的旋转。
     *
     * @param x Rotation angle around X axis in radians.<br> 绕X轴旋转角度（弧度）
     * @param y Rotation angle around Y axis in radians.<br> 绕Y轴旋转角度（弧度）
     * @param z Rotation angle around Z axis in radians.<br> 绕Z轴旋转角度（弧度）
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform setRotationRadians(float x, float y, float z) {
        this.rotation.identity()
                .rotateYXZ(y, x, z);
        this.isDirty = true;
        return this;
    }

    /**
     * Rotates this transform by the given angle around the specified axis.<br>
     * 按指定轴和角度旋转此变换。
     *
     * @param angle The rotation angle in radians.<br> 旋转角度（弧度）
     * @param axis The axis to rotate around.<br> 旋转轴
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform rotate(float angle, Vector3f axis) {
        this.rotation.rotateAxis(angle, axis);
        this.isDirty = true;
        return this;
    }

    /**
     * Rotates this transform by the given quaternion (accumulative).<br>
     * 按指定四元数旋转（累加）此变换。
     *
     * @param quaternion The quaternion to apply.<br> 要应用的四元数
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform rotate(Quaternionf quaternion) {
        this.rotation.mul(quaternion);
        this.isDirty = true;
        return this;
    }

    /**
     * Sets the scale of this transform.<br>
     * 设置此变换的缩放。
     *
     * @param x Scale along X axis.<br> X轴缩放
     * @param y Scale along Y axis.<br> Y轴缩放
     * @param z Scale along Z axis.<br> Z轴缩放
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
        this.isDirty = true;
        return this;
    }

    /**
     * Sets the scale of this transform using a Vector3f.<br>
     * 使用Vector3f设置此变换的缩放。
     *
     * @param v The scale vector.<br> 缩放向量
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform setScale(Vector3f v) {
        this.scale.set(v.x, v.y, v.z);
        this.isDirty = true;
        return this;
    }

    /**
     * Sets the uniform scale of this transform.<br>
     * 设置此变换的统一缩放。
     *
     * @param uniformScale The uniform scale value.<br> 统一缩放值
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform setScale(float uniformScale) {
        this.scale.set(uniformScale, uniformScale, uniformScale);
        this.isDirty = true;
        return this;
    }

    /**
     * Sets all transform components at once.<br>
     * 一次性设置所有变换组件。
     *
     * @param position The position vector.<br> 位置向量
     * @param rotation The rotation quaternion.<br> 旋转四元数
     * @param scale The scale vector.<br> 缩放向量
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform set(Vector3f position, Quaternionf rotation, Vector3f scale) {
        this.position.set(position);
        this.rotation.set(rotation);
        this.scale.set(scale);
        this.isDirty = true;
        return this;
    }

    /**
     * Copies all transform components from another Transform.<br>
     * 从另一个Transform对象复制所有变换组件。
     *
     * @param other The source Transform.<br> 源Transform对象
     * @return This transform instance for chaining.<br> 返回当前变换对象以支持链式调用。
     */
    public Transform set(Transform other) {
        this.position.set(other.position);
        this.rotation.set(other.rotation);
        this.scale.set(other.scale);
        this.isDirty = true;
        return this;
    }

    /**
     * Gets the model matrix for this transform.<br>
     * 获取此变换的模型矩阵。
     *
     * @return The model matrix.<br> 模型矩阵
     */
    public Matrix4f getModelMatrix() {
        if (isDirty) {
            modelMatrix.identity()
                    .translate(position)
                    .rotate(rotation)
                    .scale(scale);
            isDirty = false;
        }
        return modelMatrix;
    }
}
