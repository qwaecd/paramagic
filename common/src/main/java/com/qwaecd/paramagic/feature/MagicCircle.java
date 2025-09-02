package com.qwaecd.paramagic.feature;


import com.qwaecd.paramagic.client.renderer.MagicCircleRenderer;
import org.joml.Matrix4f;

import java.util.*;

public class MagicCircle extends MagicNode {
    private final Map<String, MagicNode> nodeRegistry = new HashMap<>();

    public MagicCircle() {
        super(null, null);
    }

    @Override
    public void addChild(MagicNode child) {
        if (child == null) {
            throw new IllegalArgumentException("MagicNode cannot be null.");
        }
        if (child instanceof MagicCircle) {
            throw new IllegalArgumentException("Cannot add a MagicCircle as a child to another MagicCircle.");
        }
        super.addChild(child);
    }

    @Override
    public void draw(Matrix4f parentWorldTransform, MagicCircleRenderer renderer) {
        super.draw(parentWorldTransform, renderer);
    }

    /**
     * Registers a node with its component ID for later lookup.<br>
     * 注册一个节点及其组件ID以供后续查找。
     *
     * @param componentId The component ID.<br> 组件ID。
     * @param node The node to register.<br> 要注册的节点。
     */
    public void registerNode(String componentId, MagicNode node) {
        if (componentId != null && !componentId.isEmpty()) {
            nodeRegistry.put(componentId, node);
        }
    }
    /**
     * Finds a node by its component ID.<br>
     * 根据组件ID查找节点。
     *
     * @param componentId The component ID to search for.<br> 要搜索的组件ID。
     * @return The found node, or Optional.empty() if not found.<br> 找到的节点，如果未找到则返回Optional.empty()。
     */
    public Optional<MagicNode> findNodeById(String componentId) {
        return Optional.ofNullable(nodeRegistry.get(componentId));
    }
    /**
     * Gets all registered component IDs.<br>
     * 获取所有已注册的组件ID。
     *
     * @return A set of component IDs.<br> 组件ID集合。
     */
    public Set<String> getRegisteredComponentIds() {
        return Collections.unmodifiableSet(nodeRegistry.keySet());
    }
    /**
     * Clears all registered nodes.<br>
     * 清除所有已注册的节点。
     */
    public void clearNodeRegistry() {
        nodeRegistry.clear();
    }
}
