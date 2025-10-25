package com.qwaecd.paramagic.feature.circle;


import com.qwaecd.paramagic.client.renderer.MagicCircleRenderer;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.*;

public class MagicCircle extends MagicNode {
    private final MagicNodeRegistry nodeRegistry = new MagicNodeRegistry();

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
        this.nodeRegistry.registerNode(componentId, node);
    }
    /**
     * Finds a node by its component ID.<br>
     * 根据组件ID查找节点。
     *
     * @param componentId The component ID to search for.<br> 要搜索的组件ID。
     * @return The found node, or Optional.empty() if not found.<br> 找到的节点，如果未找到则返回Optional.empty()。
     */
    public Optional<MagicNode> findNodeById(String componentId) {
        return Optional.ofNullable(nodeRegistry.findNodeById(componentId));
    }

    /**
     * Finds a node by its component name.<br>
     * 根据组件名称查找节点。
     *
     * @return The found node, or Optional.empty() if not found.<br> 找到的节点，如果未找到则返回Optional.empty()。
     */
    public Optional<MagicNode> findNodeByName(String name) {
        return Optional.ofNullable(nodeRegistry.findNodeByName(name));
    }

    public Optional<MagicNode> findNode(String key) {
        return Optional.ofNullable(nodeRegistry.findNode(key));
    }

    /**
     * Gets all registered component IDs.<br>
     * 获取所有已注册的组件ID。
     *
     * @return A set of component IDs.<br> 组件ID集合。
     */
    public Set<String> getRegisteredComponentIds() {
        return this.nodeRegistry.getRegisteredComponentIds();
    }

    static class MagicNodeRegistry {
        // 路径ID到MagicNode的映射
        private final Map<String, MagicNode> nodesByPath = new HashMap<>();

        // 名称到MagicNode的映射（仅包含有名称的节点）
        private final Map<String, MagicNode> nodesByName = new HashMap<>();

        private MagicNodeRegistry() {}

        private void registerNode(String componentId, MagicNode node) {
            if (componentId != null && !componentId.isEmpty()) {
                nodesByPath.put(componentId, node);
            }

            String nodeName = node.getName();
            if (nodeName != null && !nodeName.isEmpty()) {
                nodesByName.put(nodeName, node);
            }
        }

        @Nullable
        MagicNode findNodeById(String componentId) {
            return nodesByPath.get(componentId);
        }

        @Nullable
        MagicNode findNodeByName(String nodeName) {
            return nodesByName.get(nodeName);
        }

        @Nullable
        MagicNode findNode(String key) {
            MagicNode node;
            node = findNodeByName(key);
            if (node == null) {
                node = findNodeById(key);
            }
            return node;
        }
        Set<String> getRegisteredComponentIds() {
            return Collections.unmodifiableSet(nodesByPath.keySet());
        }
    }
}
