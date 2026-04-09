package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorItemProvider;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;

public abstract class ModifierOperator extends ParaOperator {
    public ModifierOperator(@Nonnull ParaOpId id, @Nonnull ItemLike item) {
        super(id, item);
    }

    public ModifierOperator(@Nonnull ParaOpId id, @Nonnull OperatorItemProvider provider) {
        super(id, provider);
    }

    /**
     * 当实体因为卸载而保存后，重新加载到世界中时被调用，需要使用该函数恢复运行时修正函数
     * @param entity 被重新加载的实体
     */
    public abstract void rebuild(ProjectileEntity entity);
}
