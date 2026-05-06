package com.qwaecd.paramagic.world.explosion;

import net.minecraft.server.level.ServerLevel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class CustomExplosionManager {
    private static final CustomExplosionManager INSTANCE = new CustomExplosionManager();

    private final List<CustomExplosionTask> tasks = new LinkedList<>();

    private CustomExplosionManager() {
    }

    public static CustomExplosionManager getInstance() {
        return INSTANCE;
    }

    public static void tick(ServerLevel level) {
        INSTANCE.tickLevel(level);
    }

    void addTask(CustomExplosionTask task) {
        this.tasks.add(task);
    }

    private void tickLevel(ServerLevel level) {
        Iterator<CustomExplosionTask> iterator = this.tasks.iterator();
        while (iterator.hasNext()) {
            CustomExplosionTask task = iterator.next();
            if (task.getLevel() != level) {
                continue;
            }
            task.tick();
            if (task.isFinished()) {
                iterator.remove();
            }
        }
    }
}
