package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

/**
 * Requests that the server open the spell editor for the sender's offhand
 * crystal. The packet deliberately carries no client-controlled item or
 * position data; the server derives both from the authenticated player.
 */
public final class C2SOpenSpellEditMenuPacket implements Packet<C2SOpenSpellEditMenuPacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(
            ModRL.inModSpace("open_spell_edit_menu")
    );

    @Override
    public void encode(DataCodec codec) {
        // No payload: the server only trusts the sender's current offhand item.
    }

    public static C2SOpenSpellEditMenuPacket decode(DataCodec codec) {
        return new C2SOpenSpellEditMenuPacket();
    }

    @Override
    public Class<C2SOpenSpellEditMenuPacket> getPacketClass() {
        return C2SOpenSpellEditMenuPacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
