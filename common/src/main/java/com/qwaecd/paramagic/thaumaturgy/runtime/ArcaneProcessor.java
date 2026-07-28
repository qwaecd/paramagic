package com.qwaecd.paramagic.thaumaturgy.runtime;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.node.ParaSpellTree;
import com.qwaecd.paramagic.thaumaturgy.node.SpellNode;
import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.alpha.MultiCastOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.flow.BranchOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.flow.CycleOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * 术式行执行器。
 *
 * <p>执行器始终在服务器线程上运行。所谓分支并行是指多个 {@link ExecutionLane}
 * 按创建顺序在同一个列表中推进，主脉络固定为列表第一个元素。</p>
 */
public class ArcaneProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArcaneProcessor.class);
    private static final float DELTA_TIME = 0.05f;
    private static final int MAX_LIVE_BRANCH_LANES = 64;
    private static final int MAX_MODIFIER_STACK_DEPTH = 256;

    @Nonnull
    private final ParaSpellTree tree;
    @Nonnull
    private final ParaContext context;
    @Nonnull
    private final List<ExecutionLane> lanes = new ArrayList<>();

    @Nullable
    private ExecutionLane main;
    private long nextLaneId;

    public ArcaneProcessor(@Nonnull ParaSpellTree tree, @Nonnull ParaContext context) {
        this.tree = tree;
        this.context = context;
    }

    public void init() {
        this.lanes.clear();
        this.nextLaneId = 0L;

        SpellNode root = this.tree.getRoot();
        if (root.getOperator() == null) {
            this.main = null;
            return;
        }

        this.main = this.createMainLane(root);
        this.lanes.add(this.main);
    }

    public void tick() {
        for (ExecutionLane lane : this.lanes) {
            if (lane.transmissionDelay > 0.0f) {
                lane.transmissionDelay -= DELTA_TIME;
            }
        }

        // 新建分支会追加至 lanes 尾部，因此可在其创建者之后的同一 tick 获得推进机会。
        // 在循环中可能修改被循环的列表
        //noinspection ForLoopReplaceableByForEach
        for (int index = 0; index < this.lanes.size(); index++) {
            ExecutionLane lane = this.lanes.get(index);
            if (lane.state != LaneState.DESTROYED) {
                this.advanceLane(lane);
                this.releaseLaneProjectiles(lane);
            }
        }

        this.removeDestroyedLanes();
    }

    private void advanceLane(@Nonnull ExecutionLane lane) {
        switch (lane.state) {
            case WAITING_TRANSMISSION -> {
                if (lane.transmissionDelay > 0.0f) {
                    return;
                }
                lane.state = LaneState.RUNNING;
            }
            case WAITING_MAIN_CYCLE -> {
                this.advanceMainCycle(lane);
                return;
            }
            case WAITING_COMPLETION -> {
                this.advanceBranchCompletion(lane);
                return;
            }
            case DESTROYED -> {
                return;
            }
            case RUNNING -> {
                // continue below
            }
        }

        if (lane.bg <= 0) {
            lane.bg = 1;
        }

        while (lane.bg > 0 && lane.state == LaneState.RUNNING) {
            LineFrame frame = lane.frames.peek();
            if (frame == null) {
                this.finishCurrentLane(lane);
                return;
            }

            if (frame.cursor >= frame.nodes.size()) {
                lane.frames.pop();
                continue;
            }

            SpellNode node = frame.nodes.get(frame.cursor++);
            boolean shouldEnterChildren = this.mayEnterChildren(lane, node);

            // 根节点的直属子节点已与根拼入首行，不能再次压入，否则根的子节点会重复执行。
            if (shouldEnterChildren && node != frame.inlineChildrenRoot && !node.getChildren().isEmpty()) {
                lane.frames.push(LineFrame.childrenOf(node));
            }
        }
        if (lane.transmissionDelay > 0.0f) {
            lane.state = LaneState.WAITING_TRANSMISSION;
        }
    }

    /**
     * @return 当前节点是否允许其子行继续参与解释
     */
    private boolean mayEnterChildren(@Nonnull ExecutionLane lane, @Nonnull SpellNode node) {
        ParaOperator operator = node.getOperator();
        if (operator == null) {
            return false;
        }

        if (operator instanceof BranchOperator) {
            this.executeBranch(lane, node, operator);
            // 分支的子行属于新建支脉络，不能再由创建者作为普通子树读取。
            return false;
        }
        if (operator instanceof CycleOperator) {
            this.executeCycle(lane, operator);
            return true;
        }
        if (operator instanceof MultiCastOperator multiCast) {
            this.executeMultiCast(lane, multiCast);
            return true;
        }

        switch (operator.getType()) {
            case PROJECTILE -> this.executeProjectile(lane, operator);
            case MODIFIER, ALPHA -> this.prepareSpell(lane, node, operator);
            case FLOW -> this.executeFlow(lane, operator);
        }
        // 节点施放失败只代表本节点没有效果，不代表其下方子术式非法。
        return true;
    }

    private void prepareSpell(@Nonnull ExecutionLane lane, @Nonnull SpellNode node, @Nonnull ParaOperator operator) {
        if (lane.modifierStack.size() >= MAX_MODIFIER_STACK_DEPTH) {
            this.warnUnstable("modifier stack limit reached", lane);
            return;
        }
        if (!this.context.caster.tryConsumeMana(operator.getManaCost())) {
            return;
        }

        lane.modifierStack.add(new PreparedSpell(node));
        this.addTime(lane, operator);
    }

    private void executeProjectile(@Nonnull ExecutionLane lane, @Nonnull ParaOperator operator) {
        boolean hasMana = this.context.caster.tryConsumeMana(operator.getManaCost());
        if (hasMana) {
            if (this.context.appendProjectileBatch(operator, lane.pendingProjectiles)) {
                this.addTime(lane, operator);
            }
        }

        // 无论投射物是否因魔力不足或创建失败而失败，均消耗一次投射物额度。
        lane.bg--;
    }

    private void executeFlow(@Nonnull ExecutionLane lane, @Nonnull ParaOperator operator) {
        if (!this.context.caster.tryConsumeMana(operator.getManaCost())) {
            return;
        }
        if (operator.apply(this.context)) {
            this.addTime(lane, operator);
        }
    }

    private void executeMultiCast(@Nonnull ExecutionLane lane, @Nonnull MultiCastOperator operator) {
        if (!this.context.caster.tryConsumeMana(operator.getManaCost())) {
            return;
        }
        lane.bg += operator.getAdditionalCasts();
        this.addTime(lane, operator);
    }

    private void executeCycle(@Nonnull ExecutionLane lane, @Nonnull ParaOperator operator) {
        // 主脉络本身已拥有自动回转；回转法术只赋予支脉络继续存在的权力。
        if (lane == this.main) {
            return;
        }
        if (!this.context.caster.tryConsumeMana(operator.getManaCost())) {
            return;
        }
        lane.restartRequested = true;
        this.addTime(lane, operator);
    }

    private void executeBranch(@Nonnull ExecutionLane parent, @Nonnull SpellNode branchNode, @Nonnull ParaOperator operator) {
        if (this.getLiveBranchCount() >= MAX_LIVE_BRANCH_LANES) {
            this.warnUnstable("branch lane limit reached", parent);
            return;
        }
        if (!this.context.caster.tryConsumeMana(operator.getManaCost())) {
            return;
        }

        ExecutionLane child = new ExecutionLane(
                this.nextLaneId++,
                parent,
                LineFrame.childrenOf(branchNode)
        );
        // 分支复制的是已经准备完成的施法状态；不重新读取它们，故不再次支付魔力或双延迟。
        child.modifierStack.addAll(parent.modifierStack);
        parent.liveChildCount++;
        this.lanes.add(child);
        this.addTime(parent, operator);
    }

    @Nonnull
    private List<ParaOperator> createModifierOperators(@Nonnull List<PreparedSpell> preparedSpells) {
        List<ParaOperator> result = new ArrayList<>(preparedSpells.size());
        for (PreparedSpell spell : preparedSpells) {
            ParaOperator operator =
                    AllParaOperators.createOperator(Objects.requireNonNull(
                            spell.node.getOperator(), "Operator cannot be null"
                    ).getId());
            if (operator != null) {
                result.add(operator);
            }
        }
        return result;
    }

    private void finishCurrentLane(@Nonnull ExecutionLane lane) {
        if (lane == this.main) {
            lane.state = LaneState.WAITING_MAIN_CYCLE;
        } else {
            lane.state = LaneState.WAITING_COMPLETION;
        }
    }

    private void advanceMainCycle(@Nonnull ExecutionLane lane) {
        if (lane.transmissionDelay > 0.0f) {
            return;
        }
        this.tickCycleCooldown(lane);
        if (lane.cycleCooldown > 0.0f) {
            return;
        }

        SpellNode root = this.tree.getRoot();
        if (root.getOperator() == null) {
            return;
        }
        lane.frames.clear();
        lane.frames.push(LineFrame.mainFunction(root));
        lane.state = LaneState.RUNNING;
    }

    private void advanceBranchCompletion(@Nonnull ExecutionLane lane) {
        if (lane.transmissionDelay > 0.0f || lane.liveChildCount > 0) {
            return;
        }

        if (lane.restartRequested) {
            this.tickCycleCooldown(lane);
            if (lane.cycleCooldown > 0.0f) {
                return;
            }
            lane.frames.clear();
            lane.frames.push(lane.initialFrame.copy());
            lane.state = LaneState.RUNNING;
            return;
        }

        this.returnToParent(lane);
    }

    private void returnToParent(@Nonnull ExecutionLane child) {
        ExecutionLane parent = child.parent;
        if (parent == null) {
            child.state = LaneState.DESTROYED;
            return;
        }

        int remainingCapacity = MAX_MODIFIER_STACK_DEPTH - parent.modifierStack.size();
        if (remainingCapacity < child.modifierStack.size()) {
            this.warnUnstable("modifier stack limit reached during branch return", parent);
        }
        for (int i = 0; i < child.modifierStack.size() && i < remainingCapacity; i++) {
            parent.modifierStack.add(child.modifierStack.get(i));
        }
        parent.liveChildCount--;
        child.state = LaneState.DESTROYED;
    }

    private void addTime(@Nonnull ExecutionLane lane, @Nonnull ParaOperator operator) {
        lane.cycleCooldown += operator.getCycleCooldown();
        lane.transmissionDelay += operator.getTransmissionDelay();
    }

    /**
     * 回转冷却与传导延迟相同：负值代表有效时间抵扣，立即放行但保留数值。
     */
    private void tickCycleCooldown(@Nonnull ExecutionLane lane) {
        if (lane.cycleCooldown > 0.0f) {
            lane.cycleCooldown -= DELTA_TIME;
        }
    }

    /**
     * 每条脉络在自己的本 tick 推进结束后独立提交投射物批次。
     * 因此同一条脉络内通过多个 bg 构造的投射物共享同一份修正栈，
     * 但不同脉络绝不会共享投射物或修正。
     */
    private void releaseLaneProjectiles(@Nonnull ExecutionLane lane) {
        if (lane.pendingProjectiles.isEmpty()) {
            return;
        }
        this.context.releaseProjectileBatch(lane.pendingProjectiles, this.createModifierOperators(lane.modifierStack));
        lane.pendingProjectiles.clear();
        lane.modifierStack.clear();
    }

    private int getLiveBranchCount() {
        int count = 0;
        for (ExecutionLane lane : this.lanes) {
            if (lane != this.main && lane.state != LaneState.DESTROYED) {
                count++;
            }
        }
        return count;
    }

    private void removeDestroyedLanes() {
        Iterator<ExecutionLane> iterator = this.lanes.iterator();
        while (iterator.hasNext()) {
            ExecutionLane lane = iterator.next();
            if (lane != this.main && lane.state == LaneState.DESTROYED) {
                iterator.remove();
            }
        }
    }

    private void warnUnstable(@Nonnull String reason, @Nonnull ExecutionLane lane) {
        LOGGER.warn("Arcane spell became unstable: {} (lane={})", reason, lane.id);
    }

    @Nonnull
    private ExecutionLane createMainLane(@Nonnull SpellNode root) {
        return new ExecutionLane(this.nextLaneId++, null, LineFrame.mainFunction(root));
    }

    enum LaneState {
        RUNNING,
        WAITING_TRANSMISSION,
        WAITING_MAIN_CYCLE,
        WAITING_COMPLETION,
        DESTROYED
    }

    private static final class ExecutionLane {
        private final long id;
        @Nullable private final ExecutionLane parent;
        private final Deque<LineFrame> frames = new ArrayDeque<>();
        private final LineFrame initialFrame;
        private final List<PreparedSpell> modifierStack = new ArrayList<>();
        private final List<ProjectileEntity> pendingProjectiles = new ArrayList<>();
        private int bg;
        private float transmissionDelay;
        private float cycleCooldown;
        private int liveChildCount;
        private LaneState state = LaneState.RUNNING;
        // 由回转操作符设置；支脉络完成后据此决定回流或重启。
        private boolean restartRequested;

        private ExecutionLane(long id, @Nullable ExecutionLane parent, @Nonnull LineFrame initialFrame) {
            this.id = id;
            this.parent = parent;
            this.initialFrame = initialFrame;
            this.frames.push(initialFrame.copy());
        }
    }

    private static final class LineFrame {
        private final List<SpellNode> nodes;
        @Nullable private final SpellNode inlineChildrenRoot;
        private int cursor;

        private LineFrame(@Nonnull List<SpellNode> nodes, @Nullable SpellNode inlineChildrenRoot) {
            this.nodes = nodes;
            this.inlineChildrenRoot = inlineChildrenRoot;
        }

        @Nonnull
        private static LineFrame mainFunction(@Nonnull SpellNode root) {
            List<SpellNode> nodes = new ArrayList<>(root.getChildren().size() + 1);
            nodes.add(root);
            nodes.addAll(root.getChildren());
            return new LineFrame(nodes, root);
        }

        @Nonnull
        private static LineFrame childrenOf(@Nonnull SpellNode root) {
            return new LineFrame(new ArrayList<>(root.getChildren()), null);
        }

        @Nonnull
        private LineFrame copy() {
            return new LineFrame(this.nodes, this.inlineChildrenRoot);
        }
    }

    private static final class PreparedSpell {
        @Nonnull private final SpellNode node;

        private PreparedSpell(@Nonnull SpellNode node) {
            this.node = node;
        }
    }

/*
    Legacy depth-first implementation retained for reference. It deliberately remains
    commented out: the new processor has no compatibility path back to type-priority DFS.

    private final Deque<SpellNode> executionStack = new ArrayDeque<>();
    private final Map<SpellNode, NodeState> nodeStates = new IdentityHashMap<>();

    public void tick() {
        final float deltaTime = 0.05f;
        this.transmissionDelay = Math.max(0.0f, this.transmissionDelay - deltaTime);
        if (this.isCycleCoolingDown) { ... return; }
        if (this.transmissionDelay > 0.0f) { return; }
        int depthBudget = 1;
        while (depthBudget > 0) {
            SpellNode stackTop = this.peekNode();
            if (stackTop == null) { this.reset(); return; }
            SpellNode targetNode = this.findNextCandidate(stackTop);
            if (targetNode != null) {
                for (SpellNode child : stackTop.getChildren()) {
                    if (this.getState(child) == NodeState.PENDING) {
                        this.addTime(child.getOperator());
                        this.setState(child, NodeState.VISITED);
                        this.context.addOperator(child.getOperator());
                    }
                }
                if (targetNode.getChildren().isEmpty()) {
                    this.setState(targetNode, NodeState.RESOLVED);
                    continue;
                }
                --depthBudget;
                this.setState(targetNode, NodeState.EVALUATING);
                this.pushNode(targetNode);
            } else {
                this.setState(stackTop, NodeState.RESOLVED);
                this.popNode();
            }
        }
        this.context.execute();
    }

    private SpellNode findNextCandidate(SpellNode parent) {
        SpellNode candidate = null;
        for (SpellNode item : parent.getChildren()) {
            if (this.getState(item) == NodeState.EVALUATING || this.getState(item) == NodeState.RESOLVED) continue;
            if (item.getOperator() == null) continue;
            if (candidate == null || item.getOperator().getType().getPriority()
                    < candidate.getOperator().getType().getPriority()) candidate = item;
        }
        return candidate;
    }
*/
}
