package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

import javax.annotation.Nonnull;

public class C2SSetSpellTreeNodeOperatorPacket implements Packet<C2SSetSpellTreeNodeOperatorPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(ModRL.inModSpace("set_spell_tree_node_operator"));

    private final int editEpoch;
    private final int seq;
    private final int baseVersion;
    @Nonnull
    private final String nodeId;
    @Nonnull
    private final SetOperatorAction action;

    public C2SSetSpellTreeNodeOperatorPacket(
            int editEpoch,
            int seq,
            int baseVersion,
            @Nonnull String nodeId,
            @Nonnull SetOperatorAction action
    ) {
        this.editEpoch = editEpoch;
        this.seq = seq;
        this.baseVersion = baseVersion;
        this.nodeId = nodeId;
        this.action = action;
    }

    public int getEditEpoch() {
        return this.editEpoch;
    }

    public int getSeq() {
        return this.seq;
    }

    public int getBaseVersion() {
        return this.baseVersion;
    }

    @Nonnull
    public String getNodeId() {
        return this.nodeId;
    }

    @Nonnull
    public SetOperatorAction getAction() {
        return this.action;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeInt("editEpoch", this.editEpoch);
        codec.writeInt("seq", this.seq);
        codec.writeInt("baseVersion", this.baseVersion);
        codec.writeString("nodeId", this.nodeId);
        codec.writeString("action", this.action.name());
    }

    public static C2SSetSpellTreeNodeOperatorPacket decode(DataCodec codec) {
        int editEpoch = codec.readInt("editEpoch");
        int seq = codec.readInt("seq");
        int baseVersion = codec.readInt("baseVersion");
        String nodeId = codec.readString("nodeId");
        SetOperatorAction action = SetOperatorAction.valueOf(codec.readString("action"));
        return new C2SSetSpellTreeNodeOperatorPacket(editEpoch, seq, baseVersion, nodeId, action);
    }

    @Override
    public Class<C2SSetSpellTreeNodeOperatorPacket> getPacketClass() {
        return C2SSetSpellTreeNodeOperatorPacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
