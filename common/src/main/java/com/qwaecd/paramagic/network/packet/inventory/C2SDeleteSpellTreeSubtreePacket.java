package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

import javax.annotation.Nonnull;

public class C2SDeleteSpellTreeSubtreePacket implements Packet<C2SDeleteSpellTreeSubtreePacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(ModRL.inModSpace("delete_spell_tree_subtree"));

    private final int editEpoch;
    private final int seq;
    private final int baseVersion;
    @Nonnull
    private final String nodeId;

    public C2SDeleteSpellTreeSubtreePacket(int editEpoch, int seq, int baseVersion, @Nonnull String nodeId) {
        this.editEpoch = editEpoch;
        this.seq = seq;
        this.baseVersion = baseVersion;
        this.nodeId = nodeId;
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

    @Override
    public void encode(DataCodec codec) {
        codec.writeInt("editEpoch", this.editEpoch);
        codec.writeInt("seq", this.seq);
        codec.writeInt("baseVersion", this.baseVersion);
        codec.writeString("nodeId", this.nodeId);
    }

    public static C2SDeleteSpellTreeSubtreePacket decode(DataCodec codec) {
        int editEpoch = codec.readInt("editEpoch");
        int seq = codec.readInt("seq");
        int baseVersion = codec.readInt("baseVersion");
        String nodeId = codec.readString("nodeId");
        return new C2SDeleteSpellTreeSubtreePacket(editEpoch, seq, baseVersion, nodeId);
    }

    @Override
    public Class<C2SDeleteSpellTreeSubtreePacket> getPacketClass() {
        return C2SDeleteSpellTreeSubtreePacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
