package com.qwaecd.paramagic.spell.state.internal.event.queue;

import com.qwaecd.paramagic.spell.state.internal.event.MachineEvent;
import lombok.Getter;

import javax.annotation.Nonnull;

public final class MachineEventEnvelope implements Comparable<MachineEventEnvelope> {
    @Getter
    final MachineEvent event;
    /**
     * 用于标记绑定的 phase 的字段, -1 表示未绑定任何 phase.
     * 区分状态切换后, 是否被判定为旧事件.
     */
    @Getter
    final long generation;

    MachineEventEnvelope(MachineEvent event, long generation) {
        this.event = event;
        this.generation = generation;
    }

    @Override
    public int compareTo(@Nonnull MachineEventEnvelope o) {
        return event.compareTo(o.event);
    }
}
