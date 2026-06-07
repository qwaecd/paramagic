package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

import javax.annotation.Nonnull;

public class S2CSpellTreeEditRejectedPacket implements Packet<S2CSpellTreeEditRejectedPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInClient(ModRL.inModSpace("spell_tree_edit_rejected"));

    private final int editEpoch;
    private final int serverVersion;
    @Nonnull
    private final SpellTreeEditRejectReason reason;

    public S2CSpellTreeEditRejectedPacket(
            int editEpoch,
            int serverVersion,
            @Nonnull SpellTreeEditRejectReason reason
    ) {
        this.editEpoch = editEpoch;
        this.serverVersion = serverVersion;
        this.reason = reason;
    }

    public int getEditEpoch() {
        return this.editEpoch;
    }

    public int getServerVersion() {
        return this.serverVersion;
    }

    @Nonnull
    public SpellTreeEditRejectReason getReason() {
        return this.reason;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeInt("editEpoch", this.editEpoch);
        codec.writeInt("serverVersion", this.serverVersion);
        codec.writeString("reason", this.reason.name());
    }

    public static S2CSpellTreeEditRejectedPacket decode(DataCodec codec) {
        int editEpoch = codec.readInt("editEpoch");
        int serverVersion = codec.readInt("serverVersion");
        SpellTreeEditRejectReason reason = SpellTreeEditRejectReason.valueOf(codec.readString("reason"));
        return new S2CSpellTreeEditRejectedPacket(editEpoch, serverVersion, reason);
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
