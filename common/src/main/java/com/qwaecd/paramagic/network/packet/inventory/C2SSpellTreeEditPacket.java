package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

import javax.annotation.Nonnull;

/**
 * The single protocol boundary for the current wand spell-tree UI.
 * Item identity and stack counts are deliberately not trusted from the client.
 */
public final class C2SSpellTreeEditPacket implements Packet<C2SSpellTreeEditPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(ModRL.inModSpace("spell_tree_edit"));

    private final int editEpoch;
    private final int requestId;
    private final int baseVersion;
    @Nonnull private final SpellTreeEditOperation operation;
    @Nonnull private final String nodeId;
    private final int childIndex;

    public C2SSpellTreeEditPacket(
            int editEpoch, int requestId, int baseVersion,
            @Nonnull SpellTreeEditOperation operation, @Nonnull String nodeId, int childIndex
    ) {
        this.editEpoch = editEpoch;
        this.requestId = requestId;
        this.baseVersion = baseVersion;
        this.operation = operation;
        this.nodeId = nodeId;
        this.childIndex = childIndex;
    }

    public int getEditEpoch() { return this.editEpoch; }
    public int getRequestId() { return this.requestId; }
    public int getBaseVersion() { return this.baseVersion; }
    @Nonnull public SpellTreeEditOperation getOperation() { return this.operation; }
    @Nonnull public String getNodeId() { return this.nodeId; }
    public int getChildIndex() { return this.childIndex; }

    @Override
    public void encode(DataCodec codec) {
        codec.writeInt("editEpoch", this.editEpoch);
        codec.writeInt("requestId", this.requestId);
        codec.writeInt("baseVersion", this.baseVersion);
        codec.writeString("operation", this.operation.name());
        codec.writeString("nodeId", this.nodeId);
        codec.writeInt("childIndex", this.childIndex);
    }

    public static C2SSpellTreeEditPacket decode(DataCodec codec) {
        return new C2SSpellTreeEditPacket(
                codec.readInt("editEpoch"),
                codec.readInt("requestId"),
                codec.readInt("baseVersion"),
                SpellTreeEditOperation.valueOf(codec.readString("operation")),
                codec.readString("nodeId"),
                codec.readInt("childIndex")
        );
    }

    @Override public Class<C2SSpellTreeEditPacket> getPacketClass() { return C2SSpellTreeEditPacket.class; }
    @Override public PacketIdentifier getIdentifier() { return IDENTIFIER; }
}
