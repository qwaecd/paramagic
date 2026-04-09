package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public interface ProjectileEntity {
    PhysicsProvider physics();

    /**
     * 发射散布角，单位是度，不是弧度
     */
    float getInaccuracy();

    /**
     * 发射散布角，单位是度，不是弧度
     */
    void setInaccuracy(float value);

    void setPosition(float x, float y, float z);

    default void setPosition(Vector3f v) {
        this.setPosition(v.x, v.y, v.z);
    }

    /**
     * 将投射物射出到世界中
     */
    void shoot();

    /**
     * 记录一个作用于该投射物的操作符，以便从持久化数据重建时能恢复运行时修正函数
     * @param operator 需要被记录的操作符
     */
    void recordOperator(ParaOperator operator);

    default void setOwner(@Nullable Entity owner) {
        if (this instanceof Projectile projectile) {
            projectile.setOwner(owner);
        }
    }
}
