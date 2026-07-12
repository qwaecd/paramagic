package com.qwaecd.paramagic.thaumaturgy.operator;

import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class ParaOperator {
    @Getter
    @Nonnull
    public final ParaOpId id;
    protected final OperatorItemProvider provider;
    private ItemStack renderStack;

    public ParaOperator(@Nonnull ParaOpId id, @Nonnull OperatorItemProvider provider) {
        this.id = id;
        this.provider = provider;
    }

    public ParaOperator(@Nonnull ParaOpId id, @Nonnull Supplier<? extends ItemLike> item) {
        this(id, () -> new ItemStack(item.get()));
    }

    public ParaOperator(@Nonnull ParaOpId id, @Nonnull ItemLike item) {
        this(id, () -> item);
    }

    public final OperatorType getType() {
        return this.id.type;
    }

    public ItemStack getRenderStack() {
        if (this.renderStack == null) {
            this.renderStack = this.provider.createOperatorItem();
        }
        return this.renderStack;
    }

    public ItemStack createOperatorItem() {
        return this.provider.createOperatorItem();
    }

    /**
     * 实现法术的实际作用效果，如无特别机制，不应对施法者的魔力产生影响.
     */
    public boolean apply(ParaContext context) {
        return true;
    }

    public float getCycleCooldown() {
        return this.id.getCycleCooldown();
    }

    public float getTransmissionDelay() {
        return this.id.getTransmissionDelay();
    }

    public int getManaCost() {
        return this.id.getManaCost();
    }
}
