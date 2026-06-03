package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

import javax.annotation.Nonnull;

public class C2SSetSpellTreeNodeOperatorPacket implements Packet<C2SSetSpellTreeNodeOperatorPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(ModRL.inModSpace("set_spell_tree_node_operator"));

    private final int version;
    @Nonnull
    private final String nodeId;
    @Nonnull
    private final SetOperatorAction action;

    public C2SSetSpellTreeNodeOperatorPacket(
            int version,
            @Nonnull String nodeId,
            @Nonnull SetOperatorAction action
    ) {
        this.version = version;
        this.nodeId = nodeId;
        this.action = action;
    }

    public int getVersion() {
        return this.version;
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
        codec.writeInt("version", this.version);
        codec.writeString("nodeId", this.nodeId);
        codec.writeString("action", this.action.name());
    }

    public static C2SSetSpellTreeNodeOperatorPacket decode(DataCodec codec) {
        int version = codec.readInt("version");
        String nodeId = codec.readString("nodeId");
        SetOperatorAction action = SetOperatorAction.valueOf(codec.readString("action"));
        return new C2SSetSpellTreeNodeOperatorPacket(version, nodeId, action);
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
