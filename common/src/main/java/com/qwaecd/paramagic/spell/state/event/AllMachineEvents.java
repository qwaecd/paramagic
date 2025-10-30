package com.qwaecd.paramagic.spell.state.event;

public final class AllMachineEvents {
    private AllMachineEvents() {}

    public static final MachineEvent END_SPELL = new MachineEvent("end_spell", MachineEvent.Priority.HIGH);
    public static final MachineEvent START_CASTING = new MachineEvent("start_casting", MachineEvent.Priority.MID);
    public static final MachineEvent CASTING_COMPLETE = new MachineEvent("casting_complete", MachineEvent.Priority.MID);
    public static final MachineEvent CHANNELING_COMPLETE = new MachineEvent("channeling_complete", MachineEvent.Priority.MID);
    public static final MachineEvent INTERRUPT = new MachineEvent("interrupt", MachineEvent.Priority.HIGH);
}
