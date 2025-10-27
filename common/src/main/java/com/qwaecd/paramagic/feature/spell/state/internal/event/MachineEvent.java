package com.qwaecd.paramagic.feature.spell.state.internal.event;


import javax.annotation.Nonnull;

public class MachineEvent implements Comparable<MachineEvent> {
    public final String name;
    public final int priority;

    public MachineEvent(String name, Priority priority) {
        this(name, priority.get());
    }

    public MachineEvent(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    @Override
    public int compareTo(@Nonnull MachineEvent other) {
        // 降序排序，优先级高的在前
        return Integer.compare(other.priority, this.priority);
    }

    public enum Priority {
        HIGH(200),
        MID(100),
        LOW(0);
        private final int priority;
        Priority(int priority) {
            this.priority = priority;
        }
        public int get() {
            return priority;
        }
    }
}
