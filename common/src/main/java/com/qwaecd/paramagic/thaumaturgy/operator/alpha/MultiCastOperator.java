package com.qwaecd.paramagic.thaumaturgy.operator.alpha;

import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/** Adds further projectile-read quota to the current execution lane. */
public abstract class MultiCastOperator extends ParaOperator {
    private final int additionalCasts;

    protected MultiCastOperator(
            @Nonnull ParaOpId id,
            @Nonnull Supplier<? extends ItemLike> item,
            int additionalCasts
    ) {
        super(id, item);
        this.additionalCasts = additionalCasts;
    }

    public final int getAdditionalCasts() {
        return this.additionalCasts;
    }
}
