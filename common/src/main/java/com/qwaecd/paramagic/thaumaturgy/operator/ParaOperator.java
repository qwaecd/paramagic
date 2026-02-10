package com.qwaecd.paramagic.thaumaturgy.operator;

import lombok.Getter;

import javax.annotation.Nonnull;

public abstract class ParaOperator {
    @Getter
    @Nonnull
    public final ParaOpId id;

    public ParaOperator(@Nonnull ParaOpId id) {
        this.id = id;
    }

    public final OperatorType getType() {
        return this.id.type;
    }
}
