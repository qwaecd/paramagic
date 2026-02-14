package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.ui.inventory.SlotAction;

public class C2SSlotActionPacket implements Packet<C2SSlotActionPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(ModRL.InModSpace("slot_action"));

    private final int slotIndex;
    private final SlotAction action;
    private final String extraData;

    public C2SSlotActionPacket(int slotIndex, SlotAction action, String extraData) {
        this.slotIndex = slotIndex;
        this.action = action;
        this.extraData = extraData;
    }

    public C2SSlotActionPacket(int slotIndex, SlotAction action) {
        this(slotIndex, action, "");
    }

    public int getSlotIndex() {
        return this.slotIndex;
    }

    public SlotAction getAction() {
        return this.action;
    }

    public String getExtraData() {
        return this.extraData;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeInt("slotIndex", this.slotIndex);
        codec.writeInt("action", this.action.ordinal());
        codec.writeString("extraData", this.extraData);
    }

    public static C2SSlotActionPacket decode(DataCodec codec) {
        int slotIndex = codec.readInt("slotIndex");
        int actionOrdinal = codec.readInt("action");
        String extraData = codec.readString("extraData");
        SlotAction action = SlotAction.values()[actionOrdinal];
        return new C2SSlotActionPacket(slotIndex, action, extraData);
    }

    @Override
    public Class<C2SSlotActionPacket> getPacketClass() {
        return C2SSlotActionPacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
