package com.qwaecd.paramagic.data.para.converter.factory;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.feature.circle.MagicNode;

@FunctionalInterface
public interface NodeCreator<T extends ParaComponentData> {
    /**
     * Creates a MagicNode instance from the given component data.
     * @param data The data blueprint for the node.
     * @return A new, configured MagicNode instance.
     */
    MagicNode create(T data);
}
