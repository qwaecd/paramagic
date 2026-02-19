package com.qwaecd.paramagic.network.packet.inventory;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.api.Packet;
import com.qwaecd.paramagic.network.api.PacketIdentifier;
import com.qwaecd.paramagic.tools.ModRL;

public class C2SClickTreeNodePacket implements Packet<C2SClickTreeNodePacket> {
    public static final PacketIdentifier IDENTIFIER = PacketIdentifier.handledInServer(ModRL.inModSpace("click_tree_node"));
    private final String nodePath;

    public C2SClickTreeNodePacket(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getNodePath() {
        return this.nodePath;
    }

    @Override
    public void encode(DataCodec codec) {
        codec.writeString("nodePath", this.nodePath);
    }

    public static C2SClickTreeNodePacket decode(DataCodec codec) {
        String nodePath = codec.readString("nodePath");
        return new C2SClickTreeNodePacket(nodePath);
    }

    @Override
    public Class<C2SClickTreeNodePacket> getPacketClass() {
        return C2SClickTreeNodePacket.class;
    }

    @Override
    public PacketIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
