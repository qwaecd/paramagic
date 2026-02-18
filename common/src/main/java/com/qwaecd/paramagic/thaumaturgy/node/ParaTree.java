package com.qwaecd.paramagic.thaumaturgy.node;

import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalComponent;
import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorMapComponent;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@PlatformScope(PlatformScopeType.COMMON)
public class ParaTree {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParaTree.class);
    public static final int recursionLimit = 64;
    private final Map<String, ParaNode> nodes = new HashMap<>();

    public ParaTree(@Nonnull ParaData paraData) {
        this.buildTreeRecursively(paraData.rootComponent, null, 0);
    }

    private void buildTreeRecursively(@Nonnull ParaComponentData componentData, @Nullable ParaNode parentNode, int depth) {
        String componentId = componentData.getComponentId();
        if (depth > recursionLimit) {
            LOGGER.warn("Recursion limit exceeded while building ParaTree. Component ID: {}, Depth: {}", componentData.getComponentId(), depth);
            return;
        }

        if (this.nodes.containsKey(componentId)) {
            LOGGER.warn("Duplicate component ID detected while building ParaTree. Component ID: {}", componentId);
            return;
        }

        ParaNode currentNode = new ParaNode(componentId, parentNode);
        if (parentNode != null) {
            parentNode.addChild(currentNode);
        }
        this.nodes.put(currentNode.getId(), currentNode);

        for (ParaComponentData child : componentData.getChildren()) {
            this.buildTreeRecursively(child, currentNode, depth + 1);
        }

        currentNode.freeze();
    }

    @Nonnull
    public ParaNode getRootNode() {
        return this.nodes.get(ParaData.PARENT_ID);
    }

    public void updateAll(ParaCrystalComponent component) {
        for (ParaNode node : this.nodes.values()) {
            ParaOpId paraOpId = component.getOperatorId(node.getId());
            if (paraOpId == null) {
                node.setOperator(null);
                continue;
            }
            ParaOperator op = AllParaOperators.createOperator(paraOpId);
            node.setOperator(op);
        }
    }
}
