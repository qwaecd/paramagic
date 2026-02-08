package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

import javax.annotation.Nonnull;

@PlatformScope(PlatformScopeType.COMMON)
public class ParaNode {
    @Nonnull
    private final String componentId;

    public ParaNode(@Nonnull String componentId) {
        this.componentId = componentId;
    }

    @Nonnull
    public String getId() {
        return this.componentId;
    }
}
