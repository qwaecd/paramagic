package com.qwaecd.paramagic.feature;


import com.qwaecd.paramagic.api.ExecutionResult;
import com.qwaecd.paramagic.api.ManaContext;
import com.qwaecd.paramagic.config.Config;
import com.qwaecd.paramagic.network.NetworkHandler;
import com.qwaecd.paramagic.network.SpellExecutionPacket;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayDeque;
import java.util.Queue;


public class SpellExecutor {
    private static final int MAX_DEPTH = Config.getMaxDepth();

    private static final Queue<DelayedExecution> delayedExecutions = new ArrayDeque<>();

    public static ExecutionResult executeSpell(ManaNode rootNode, ManaContext context) {
        return executeNode(rootNode, context, 0);
    }

    private static ExecutionResult executeNode(ManaNode node, ManaContext context, int depth) {
        if (depth > MAX_DEPTH) {
            return new ExecutionResult(false, "Maximum recursion depth exceeded", 0);
        }

        if (context.getAvailableMana() < node.getMagicMap().getManaCost()) {
            return new ExecutionResult(false, "Insufficient mana", 0);
        }

        try {
            // Execute current node
            node.getMagicMap().execute(context);
            int consumedMana = node.getMagicMap().getManaCost();
            context.setAvailableMana(context.getAvailableMana() - consumedMana);
            System.out.println("当前魔力："+context.getAvailableMana());
            node.setExecuted(true);

            // Send render packet to clients if needed
            if (context.getLevel() instanceof ServerLevel serverLevel) {
                SpellExecutionPacket packet = new SpellExecutionPacket(
                        node.getMagicMap().getId(),
                        context.getCenter(),
                        context.getParameters()
                );
                NetworkHandler.sendToClientsInRange(serverLevel, context.getCenter(), packet);
            }

            // Execute children with delay
            int totalManaConsumed = consumedMana;
            for (ManaNode child : node.getChildren()) {
                if (child.getExecutionDelay() > 0) {
                    scheduleDelayedExecution(child, context, child.getExecutionDelay(), depth + 1);
                } else {
                    ExecutionResult childResult = executeNode(child, context, depth + 1);
                    if (!childResult.isSuccess()) {
                        return childResult;
                    }
                    totalManaConsumed += childResult.manaConsumed;
                }
            }

            return new ExecutionResult(true, null, totalManaConsumed);

        } catch (Exception e) {
            return new ExecutionResult(false, "Execution error: " + e.getMessage(), 0);
        }
    }

    private static void scheduleDelayedExecution(ManaNode node, ManaContext context, int delay, int depth) {
        long currentTick = context.getLevel().getGameTime();
        delayedExecutions.offer(new DelayedExecution(node, context, currentTick + delay, depth));
    }

    public static void tick() {
        if (delayedExecutions.isEmpty()) return;

        DelayedExecution next = delayedExecutions.peek();
        if (next != null && next.context.getLevel().getGameTime() >= next.executeAtTick) {
            delayedExecutions.poll();
            executeNode(next.node, next.context, next.depth);
        }
    }

    public static class DelayedExecution {
        public final ManaNode node;
        public final ManaContext context;
        public final long executeAtTick;
        public final int depth;

        public DelayedExecution(ManaNode node, ManaContext context, long executeAtTick, int depth) {
            this.node = node;
            this.context = context;
            this.executeAtTick = executeAtTick;
            this.depth = depth;
        }
    }
}
