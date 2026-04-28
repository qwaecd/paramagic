package com.qwaecd.paramagic.feature.circle;


import com.qwaecd.paramagic.core.render.Transform;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class MagicCircle extends MagicNode {
    private final MagicNodeRegistry nodeRegistry = new MagicNodeRegistry();

    @Nonnull
    private CircleState state;

    @Nullable
    private Consumer<Transform> transformModifier = null;

    public interface DeadAnimator {
        void animate(MagicCircle circle, float deltaTime);
    }

    @Nullable
    private DeadAnimator deadAnimator;

    public MagicCircle() {
        super(null, null);
        this.state = CircleState.ACTIVE;
        this.deadAnimator = new ScaleDeadAnimator(0.6f, new Vector3f(0.0f, 1.0f, 0.0f));
    }

    void setState(@Nonnull CircleState state) {
        this.state = state;
    }

    @Nonnull
    public CircleState getState() {
        return this.state;
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

    public boolean canRemove() {
        return this.state == CircleState.DEAD;
    }

    public void requestDestroy() {
        if (this.state != CircleState.ACTIVE) {
            return;
        }
        this.state = CircleState.EXITING;
    }

    public void setDeadAnimator(@Nullable DeadAnimator animator) {
        this.deadAnimator = animator;
    }

    @Override
    public void update(float deltaTime) {
        if (this.state == CircleState.EXITING) {
            if (this.deadAnimator != null) {
                this.deadAnimator.animate(this, deltaTime);
            } else {
                this.state = CircleState.DEAD;
            }
        } else {
            if (this.transformModifier != null) {
                this.transformModifier.accept(this.transform);
            }
        }

        super.update(deltaTime);
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

    public void registerModifyTransform(Consumer<Transform> modifier) {
        this.transformModifier = modifier;
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

    public static class ScaleDeadAnimator implements DeadAnimator {
        private final float duration;
        private float elapsed = 0.0f;
        @Nullable
        private Vector3f initialScale;
        private final Vector3f targetScale;

        public ScaleDeadAnimator(float duration) {
            this(duration, new Vector3f(0.0f));
        }

        public ScaleDeadAnimator(
                float duration,
                Vector3f targetScale
        ) {
            this.duration = duration;
            this.targetScale = new Vector3f(targetScale);
        }

        @Override
        public void animate(MagicCircle circle, float deltaTime) {
            this.elapsed += deltaTime;
            float t = Math.min(this.elapsed / this.duration, 1.0f);
            if (this.initialScale == null) {
                this.initialScale = circle.getTransform().getScale(new Vector3f());
            }
            Vector3f newScale = new Vector3f();
            this.initialScale.lerp(this.targetScale, t, newScale);
            circle.getTransform().setScale(newScale);
            if (t >= 1.0f) {
                circle.setState(CircleState.DEAD);
            }
        }
    }
}
