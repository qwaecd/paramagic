package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.thaumaturgy.spelltree.ParaSpellTreeData;

import javax.annotation.Nonnull;

public class S2CSpellTreeEditRejectedPacket implements Packet<S2CSpellTreeEditRejectedPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInClient(ModRL.inModSpace("spell_tree_edit_rejected"));

    private final int editEpoch;
    private final int requestId;
    private final int serverVersion;
    @Nonnull
    private final SpellTreeEditRejectReason reason;
    @Nonnull
    private final ParaSpellTreeData treeData;

    public S2CSpellTreeEditRejectedPacket(
            int editEpoch, int requestId,
            int serverVersion,
            @Nonnull SpellTreeEditRejectReason reason,
            @Nonnull ParaSpellTreeData treeData
    ) {
        this.editEpoch = editEpoch;
        this.requestId = requestId;
        this.serverVersion = serverVersion;
        this.reason = reason;
        this.treeData = treeData;
    }

    public int getEditEpoch() {
        return this.editEpoch;
    }

    public int getRequestId() { return this.requestId; }

    public int getServerVersion() {
        return this.serverVersion;
    }

    @Nonnull
    public SpellTreeEditRejectReason getReason() {
        return this.reason;
    }

    @Nonnull
    public ParaSpellTreeData getTreeData() { return this.treeData; }

    @Override
    public void encode(DataCodec codec) {
        codec.writeInt("editEpoch", this.editEpoch);
        codec.writeInt("requestId", this.requestId);
        codec.writeInt("serverVersion", this.serverVersion);
        codec.writeString("reason", this.reason.name());
        codec.writeObject("treeData", this.treeData);
    }

    public static S2CSpellTreeEditRejectedPacket decode(DataCodec codec) {
        int editEpoch = codec.readInt("editEpoch");
        int requestId = codec.readInt("requestId");
        int serverVersion = codec.readInt("serverVersion");
        SpellTreeEditRejectReason reason = SpellTreeEditRejectReason.valueOf(codec.readString("reason"));
        ParaSpellTreeData treeData = codec.readObject("treeData", ParaSpellTreeData::fromCodec);
        return new S2CSpellTreeEditRejectedPacket(editEpoch, requestId, serverVersion, reason, treeData);
    }

    @Override
    public Class<S2CSpellTreeEditRejectedPacket> getPacketClass() {
        return S2CSpellTreeEditRejectedPacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
