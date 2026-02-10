package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@PlatformScope(PlatformScopeType.COMMON)
public class ParaTree {
    private final Map<String, ParaNode> nodes = new HashMap<>();
    @Nonnull
    private final ParaData paraData;

    public ParaTree(@Nonnull ParaData paraData) {
        this.paraData = paraData;

        for (ParaComponentData child : paraData.rootComponent.getChildren()) {
            String componentId = child.getComponentId();
            this.nodes.put(componentId, new ParaNode(componentId));
        }
    }
}
