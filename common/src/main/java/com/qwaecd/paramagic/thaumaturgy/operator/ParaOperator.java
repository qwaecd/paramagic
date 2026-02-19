package com.qwaecd.paramagic.thaumaturgy.operator;

import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;

public abstract class ParaOperator {
    @Getter
    @Nonnull
    public final ParaOpId id;
    protected final OperatorItemProvider provider;
    private final ItemStack renderStack;

    public ParaOperator(@Nonnull ParaOpId id, @Nonnull OperatorItemProvider provider) {
        this.id = id;
        this.provider = provider;
        this.renderStack = this.provider.createOperatorItem();
    }

    public ParaOperator(@Nonnull ParaOpId id, @Nonnull ItemLike item) {
        this(id, () -> new ItemStack(item));
    }

    public final OperatorType getType() {
        return this.id.type;
    }

    public ItemStack getRenderStack() {
        return this.renderStack;
    }

    public ItemStack createOperatorItem() {
        return this.provider.createOperatorItem();
    }

    public boolean apply(ParaContext context) {
        return true;
    }
}
