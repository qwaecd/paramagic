package com.qwaecd.paramagic.network.handler;

import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.packet.mana.S2CManaSyncPacket;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.caster.client.ClientManaState;

@PlatformScope(PlatformScopeType.CLIENT)
public final class ClientManaHandlers {
    private ClientManaHandlers() {
    }

    public static void sync(S2CManaSyncPacket packet, NetworkContext context) {
        ClientManaState.update(packet.getMana(), packet.getMaxMana());
    }
}
