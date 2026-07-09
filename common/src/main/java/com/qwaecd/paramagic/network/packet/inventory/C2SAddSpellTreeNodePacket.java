package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

import javax.annotation.Nonnull;

public class C2SAddSpellTreeNodePacket implements Packet<C2SAddSpellTreeNodePacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(ModRL.inModSpace("add_spell_tree_node"));

    private final int editEpoch;
    private final int seq;
    private final int baseVersion;
    @Nonnull
    private final String expectedNodeId;
    @Nonnull
    private final String parentNodeId;
    private final int childIndex;
    private final boolean useCarriedOperator;

    public C2SAddSpellTreeNodePacket(
            int editEpoch,
            int seq,
            int baseVersion,
            @Nonnull String expectedNodeId,
            @Nonnull String parentNodeId,
            int childIndex,
            boolean useCarriedOperator
    ) {
        this.editEpoch = editEpoch;
        this.seq = seq;
        this.baseVersion = baseVersion;
        this.expectedNodeId = expectedNodeId;
        this.parentNodeId = parentNodeId;
        this.childIndex = childIndex;
        this.useCarriedOperator = useCarriedOperator;
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
    public String getExpectedNodeId() {
        return this.expectedNodeId;
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
        codec.writeInt("editEpoch", this.editEpoch);
        codec.writeInt("seq", this.seq);
        codec.writeInt("baseVersion", this.baseVersion);
        codec.writeString("expectedNodeId", this.expectedNodeId);
        codec.writeString("parentNodeId", this.parentNodeId);
        codec.writeInt("childIndex", this.childIndex);
        codec.writeBoolean("useCarriedOperator", this.useCarriedOperator);
    }

    public static C2SAddSpellTreeNodePacket decode(DataCodec codec) {
        int editEpoch = codec.readInt("editEpoch");
        int seq = codec.readInt("seq");
        int baseVersion = codec.readInt("baseVersion");
        String expectedNodeId = codec.readString("expectedNodeId");
        String parentNodeId = codec.readString("parentNodeId");
        int childIndex = codec.readInt("childIndex");
        boolean useCarriedOperator = codec.readBoolean("useCarriedOperator");
        return new C2SAddSpellTreeNodePacket(
                editEpoch,
                seq,
                baseVersion,
                expectedNodeId,
                parentNodeId,
                childIndex,
                useCarriedOperator
        );
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
