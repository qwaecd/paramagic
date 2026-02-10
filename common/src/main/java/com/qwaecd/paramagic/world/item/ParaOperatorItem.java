package com.qwaecd.paramagic.world.item;

import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;

public abstract class ParaOperatorItem extends Item {
    @Nonnull
    protected final ParaOpId operatorId;
    public ParaOperatorItem(@Nonnull ParaOpId operatorId) {
        super(new Properties());
        this.operatorId = operatorId;
    }

    @Nonnull
    public final ParaOpId getOperatorId() {
        return this.operatorId;
    }
}
