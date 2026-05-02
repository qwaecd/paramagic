package com.qwaecd.paramagic.network.handler;

import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.packet.session.C2SSessionAttachPacket;
import com.qwaecd.paramagic.network.packet.session.S2CSessionStopPacket;
import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.spell.core.SessionManagers;
import com.qwaecd.paramagic.spell.core.SessionState;
import com.qwaecd.paramagic.spell.server.ServerSession;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerSessionHandlers {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSessionHandlers.class);

    public static void attachSession(C2SSessionAttachPacket packet, NetworkContext context) {
        ServerPlayer player = context.getPlayer();
        if (player == null) {
            LOGGER.warn("Received session attach packet without player context");
            return;
        }

        ServerSession session = SessionManagers.getForServer(player.serverLevel()).getSession(packet.getSessionId());
        if (session == null) {
            Networking.get().sendToPlayer(player, new S2CSessionStopPacket(packet.getSessionId(), EndSpellReason.FAILED));
            return;
        }

        session.addTrackingPlayer(player);
        if (!session.isState(SessionState.RUNNING)) {
            EndSpellReason reason = session.getEndReason().orElse(EndSpellReason.FAILED);
            Networking.get().sendToPlayer(player, new S2CSessionStopPacket(packet.getSessionId(), reason));
            return;
        }
        session.syncDataStore(player);
    }
}
