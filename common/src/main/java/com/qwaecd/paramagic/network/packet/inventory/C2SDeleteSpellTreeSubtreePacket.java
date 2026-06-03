package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

import javax.annotation.Nonnull;

public class C2SDeleteSpellTreeSubtreePacket implements Packet<C2SDeleteSpellTreeSubtreePacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(ModRL.inModSpace("delete_spell_tree_subtree"));

    private final int version;
    @Nonnull
    private final String nodeId;

    public C2SDeleteSpellTreeSubtreePacket(int version, @Nonnull String nodeId) {
        this.version = version;
        this.nodeId = nodeId;
    }

    public int getVersion() {
        return this.version;
    }

    @Nonnull
    public String getNodeId() {
        return this.nodeId;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeInt("version", this.version);
        codec.writeString("nodeId", this.nodeId);
    }

    public static C2SDeleteSpellTreeSubtreePacket decode(DataCodec codec) {
        int version = codec.readInt("version");
        String nodeId = codec.readString("nodeId");
        return new C2SDeleteSpellTreeSubtreePacket(version, nodeId);
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
