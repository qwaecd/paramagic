package com.qwaecd.paramagic.spell.state.internal.event.machine;

import com.qwaecd.paramagic.spell.state.internal.event.MachineEvent;

public final class AllMachineEvents {
    private AllMachineEvents() {}

    public static final MachineEvent INTERRUPT = new MachineEvent("interrupt", MachineEvent.Priority.HIGH);
    public static final MachineEvent NEXT_PHASE = new MachineEvent("next_phase", MachineEvent.Priority.MID);
}
