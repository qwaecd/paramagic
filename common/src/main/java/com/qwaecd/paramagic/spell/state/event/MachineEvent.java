package com.qwaecd.paramagic.spell.state.event;


import javax.annotation.Nonnull;
import java.util.Objects;

public class MachineEvent implements Comparable<MachineEvent> {
    /**
     * 只要 name 相同就认为是同一个事件, 不考虑 priority
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MachineEvent that = (MachineEvent) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
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
