package com.qwaecd.paramagic.client;


import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class ClientSpellScheduler {
    private static final Queue<ScheduledExecution> scheduledExecutions = new PriorityQueue<>((a, b) ->
            Long.compare(a.executeAtTick, b.executeAtTick));

    private static class ScheduledExecution {
        public final String magicMapId;
        public final Vec3 center;
        public final Map<String, Object> parameters;
        public final long executeAtTick;

        public ScheduledExecution(String magicMapId, Vec3 center, Map<String, Object> parameters, long executeAtTick) {
            this.magicMapId = magicMapId;
            this.center = center;
            this.parameters = parameters;
            this.executeAtTick = executeAtTick;
        }
    }

    public static void scheduleExecution(String magicMapId, Vec3 center, Map<String, Object> parameters) {
        scheduleExecution(magicMapId, center, parameters, 0);
    }

    public static void scheduleExecution(String magicMapId, Vec3 center, Map<String, Object> parameters, int delay) {
        long currentTick = Minecraft.getInstance().level != null ?
                Minecraft.getInstance().level.getGameTime() : 0;
        scheduledExecutions.offer(new ScheduledExecution(magicMapId, center, parameters, currentTick + delay));
    }

    public static void tick() {
        if (Minecraft.getInstance().level == null || scheduledExecutions.isEmpty()) return;

        long currentTick = Minecraft.getInstance().level.getGameTime();
        while (!scheduledExecutions.isEmpty() && scheduledExecutions.peek().executeAtTick <= currentTick) {
            ScheduledExecution execution = scheduledExecutions.poll();
            executeClientSide(execution);
        }
    }

    private static void executeClientSide(ScheduledExecution execution) {
        // Create and register client-side magic circle renderer
        // This would create visual effects based on the magic map ID
        // TODO: Implement specific magic circle renderers
    }
}
