package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

import javax.annotation.Nonnull;

public class C2SAddSpellTreeNodePacket implements Packet<C2SAddSpellTreeNodePacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(ModRL.inModSpace("add_spell_tree_node"));

    private final int version;
    @Nonnull
    private final String parentNodeId;
    private final int childIndex;
    private final boolean useCarriedOperator;

    public C2SAddSpellTreeNodePacket(
            int version,
            @Nonnull String parentNodeId,
            int childIndex,
            boolean useCarriedOperator
    ) {
        this.version = version;
        this.parentNodeId = parentNodeId;
        this.childIndex = childIndex;
        this.useCarriedOperator = useCarriedOperator;
    }

    public int getVersion() {
        return this.version;
    }

    @Nonnull
    public String getParentNodeId() {
        return this.parentNodeId;
    }

    public int getChildIndex() {
        return this.childIndex;
    }

    public boolean isUseCarriedOperator() {
        return this.useCarriedOperator;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeInt("version", this.version);
        codec.writeString("parentNodeId", this.parentNodeId);
        codec.writeInt("childIndex", this.childIndex);
        codec.writeBoolean("useCarriedOperator", this.useCarriedOperator);
    }

    public static C2SAddSpellTreeNodePacket decode(DataCodec codec) {
        int version = codec.readInt("version");
        String parentNodeId = codec.readString("parentNodeId");
        int childIndex = codec.readInt("childIndex");
        boolean useCarriedOperator = codec.readBoolean("useCarriedOperator");
        return new C2SAddSpellTreeNodePacket(version, parentNodeId, childIndex, useCarriedOperator);
    }

    @Override
    public Class<C2SAddSpellTreeNodePacket> getPacketClass() {
        return C2SAddSpellTreeNodePacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
