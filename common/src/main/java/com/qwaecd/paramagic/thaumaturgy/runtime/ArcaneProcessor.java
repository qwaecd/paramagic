package com.qwaecd.paramagic.thaumaturgy.runtime;

import com.qwaecd.paramagic.thaumaturgy.node.NodeState;
import com.qwaecd.paramagic.thaumaturgy.node.ParaNode;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;

public class ArcaneProcessor {
    @Nonnull
    private final ParaTree tree;
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

    private final Deque<ParaNode> executionStack = new ArrayDeque<>();

    public ArcaneProcessor(@Nonnull ParaTree tree, @Nonnull ParaContext context) {
        this.tree = tree;
        this.context = context;
    }

    public void init() {
        ParaNode root = this.tree.getRootNode();
        ParaOperator operator = root.getOperator();
        // 如果根节点都没有操作符，那么栈理论上应该是空的，也就是什么都不会执行
        if (operator != null) {
            root.setState(NodeState.EVALUATING);
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
            ParaNode stackTop = this.peekNode();
            if (stackTop == null) {
                // 说明已经执行完成
                // 进行回转判定
                if (this.cycleCooldown < deltaTime) {
                    this.reset();
                    break;
                }
                if (!this.isCycleCoolingDown) {
                    this.isCycleCoolingDown = true;
                }
                this.cycleCooldown -= deltaTime;
                break;
            }
            ParaNode targetNode = this.findNextCandidate(stackTop);
            if (targetNode != null) {

                for (ParaNode child : stackTop.getChildren()) {
                    ParaOperator operator = child.getOperator();
                    if (operator == null) {
                        continue;
                    }
                    if (child.getState() == NodeState.PENDING) {
                        this.addTime(operator);
                        child.setState(NodeState.VISITED);
                        this.context.addOperator(operator);
                    }
                }

                if (targetNode.getChildren().isEmpty()) {
                    // 叶子节点
                    targetNode.setState(NodeState.RESOLVED);
                    continue;
                }

                // 深度++
                --depthBudget;
                targetNode.setState(NodeState.EVALUATING);
                this.pushNode(targetNode);
            } else {
                // 回溯
                stackTop.setState(NodeState.RESOLVED);
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
        this.tree.forEachNode(node -> node.setState(NodeState.PENDING));
        this.init();
    }

    private void pushNode(@Nonnull ParaNode node) {
        this.executionStack.push(node);
    }

    @Nullable
    @SuppressWarnings("UnusedReturnValue")
    private ParaNode popNode() {
        return this.executionStack.pop();
    }

    @Nullable
    private ParaNode peekNode() {
        return this.executionStack.peek();
    }

    private boolean stackIsEmpty() {
        return this.executionStack.isEmpty();
    }

    @Nullable
    private ParaNode findNextCandidate(@Nonnull ParaNode parent) {
        ParaNode candidate = null;
        for (ParaNode item : parent.getChildren()) {
            if (item.getState() == NodeState.EVALUATING || item.getState() == NodeState.RESOLVED) {
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

    private void addTime(@Nonnull ParaOperator operator) {
        this.cycleCooldown += operator.getCycleCooldown();
        this.transmissionDelay += operator.getTransmissionDelay();
    }
}
