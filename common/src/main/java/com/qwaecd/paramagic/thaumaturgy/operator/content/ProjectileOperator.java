package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.thaumaturgy.operator.OperatorItemProvider;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;

public abstract class ProjectileOperator extends ParaOperator {
    public ProjectileOperator(@Nonnull ParaOpId id, @Nonnull OperatorItemProvider provider) {
        super(id, provider);
    }

    public ProjectileOperator(@Nonnull ParaOpId id, @Nonnull ItemLike item) {
        super(id, () -> new ItemStack(item));
    }
}
