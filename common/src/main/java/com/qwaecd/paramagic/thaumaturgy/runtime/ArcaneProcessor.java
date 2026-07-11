package com.qwaecd.paramagic.thaumaturgy.runtime;

import com.qwaecd.paramagic.thaumaturgy.node.NodeState;
import com.qwaecd.paramagic.thaumaturgy.node.ParaSpellTree;
import com.qwaecd.paramagic.thaumaturgy.node.SpellNode;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;

public class ArcaneProcessor {
    @Nonnull
    private final ParaSpellTree tree;
    @Nonnull
    private final ParaContext context;

    /**
     * 回转冷却，单位为秒
     */
    private float cycleCooldown;
    /**
     * 传导延迟，单位为秒
     */
    private float transmissionDelay;

    private boolean isCycleCoolingDown;

    private final Deque<SpellNode> executionStack = new ArrayDeque<>();
    private final Map<SpellNode, NodeState> nodeStates = new IdentityHashMap<>();

    public ArcaneProcessor(@Nonnull ParaSpellTree tree, @Nonnull ParaContext context) {
        this.tree = tree;
        this.context = context;
    }

    public void init() {
        SpellNode root = this.tree.getRoot();
        ParaOperator operator = root.getOperator();
        // 如果根节点都没有操作符，那么栈理论上应该是空的，也就是什么都不会执行
        if (operator != null) {
            this.setState(root, NodeState.EVALUATING);
            // 这里不考虑传导延迟
            this.cycleCooldown += operator.getCycleCooldown();
            this.context.addOperator(operator);
            this.pushNode(root);
        }
    }


    public void tick() {
        final float deltaTime = 0.05f; // 20 ticks per second
        this.transmissionDelay = Math.max(0.0f, this.transmissionDelay - deltaTime);

        if (this.isCycleCoolingDown) {
            this.cycleCooldown = Math.max(0.0f, this.cycleCooldown - deltaTime);
            if (cycleCooldown <= 0.0f) {
                this.isCycleCoolingDown = false;
            }
            return;
        }

        if (this.transmissionDelay > 0.0f) {
            return;
        }

        int depthBudget = 1;
        while (depthBudget > 0) {
            SpellNode stackTop = this.peekNode();
            if (stackTop == null) {
                // 说明已经执行完成
                // 进行回转判定
                if (this.cycleCooldown < deltaTime) {
                    this.reset();
                    return;
                }
                if (!this.isCycleCoolingDown) {
                    this.isCycleCoolingDown = true;
                }
                this.cycleCooldown -= deltaTime;
                break;
            }
            SpellNode targetNode = this.findNextCandidate(stackTop);
            if (targetNode != null) {

                for (SpellNode child : stackTop.getChildren()) {
                    ParaOperator operator = child.getOperator();
                    if (operator == null) {
                        continue;
                    }
                    if (this.getState(child) == NodeState.PENDING) {
                        this.addTime(operator);
                        this.setState(child, NodeState.VISITED);
                        this.context.addOperator(operator);
                    }
                }

                if (targetNode.getChildren().isEmpty()) {
                    // 叶子节点
                    this.setState(targetNode, NodeState.RESOLVED);
                    continue;
                }

                // 深度++
                --depthBudget;
                this.setState(targetNode, NodeState.EVALUATING);
                this.pushNode(targetNode);
            } else {
                // 回溯
                this.setState(stackTop, NodeState.RESOLVED);
                this.popNode();
            }
        }

        this.execute();
    }

    public void execute() {
        this.context.execute();
    }

    private void reset() {
        this.isCycleCoolingDown = false;
        this.transmissionDelay = 0.0f;
        this.cycleCooldown = 0.0f;
        this.executionStack.clear();
        this.nodeStates.clear();
        this.init();
    }

    private void pushNode(@Nonnull SpellNode node) {
        this.executionStack.push(node);
    }

    @Nullable
    @SuppressWarnings("UnusedReturnValue")
    private SpellNode popNode() {
        return this.executionStack.pop();
    }

    @Nullable
    private SpellNode peekNode() {
        return this.executionStack.peek();
    }

    private boolean stackIsEmpty() {
        return this.executionStack.isEmpty();
    }

    @Nullable
    private SpellNode findNextCandidate(@Nonnull SpellNode parent) {
        SpellNode candidate = null;
        for (SpellNode item : parent.getChildren()) {
            NodeState state = this.getState(item);
            if (state == NodeState.EVALUATING || state == NodeState.RESOLVED) {
                continue;
            }

            ParaOperator operator = item.getOperator();
            if (operator == null) {
                // 操作符为 null 的节点及其子树不参与运算
                continue;
            }

            if (candidate == null ||
                operator.getType().getPriority() < candidate.getOperator().getType().getPriority())
            {
                candidate = item;
            }
        }

        return candidate;
    }

    @Nonnull
    private NodeState getState(@Nonnull SpellNode node) {
        return this.nodeStates.getOrDefault(node, NodeState.PENDING);
    }

    private void setState(@Nonnull SpellNode node, @Nonnull NodeState state) {
        this.nodeStates.put(node, state);
    }

    private void addTime(@Nonnull ParaOperator operator) {
        this.cycleCooldown += operator.getCycleCooldown();
        this.transmissionDelay += operator.getTransmissionDelay();
    }
}
