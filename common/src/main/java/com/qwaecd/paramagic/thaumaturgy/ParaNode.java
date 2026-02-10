package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PlatformScope(PlatformScopeType.COMMON)
public class ParaNode {
    @Nonnull
    private final String componentId;
    @Nullable
    private ParaOperator operator;

    public ParaNode(@Nonnull String componentId) {
        this.componentId = componentId;
    }

    @Nonnull
    public String getId() {
        return this.componentId;
    }

    @Nullable
    public ParaOperator getOperator() {
        return this.operator;
    }

    public void setOperator(@Nullable ParaOperator operator) {
        this.operator = operator;
    }
}
