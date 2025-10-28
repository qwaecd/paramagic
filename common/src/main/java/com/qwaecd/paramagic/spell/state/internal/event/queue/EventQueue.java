package com.qwaecd.paramagic.spell.state.internal.event.queue;


import com.qwaecd.paramagic.spell.state.internal.event.MachineEvent;

import javax.annotation.Nullable;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class EventQueue {
    private final Queue<MachineEventEnvelope> queue = new PriorityBlockingQueue<>();

    @Nullable
    public MachineEventEnvelope pollOne() {
        return this.queue.poll();
    }

    public void offer(MachineEvent event, long generation) {
        this.queue.offer(new MachineEventEnvelope(event, generation));
    }

    public void clear() {
        this.queue.clear();
    }
}
