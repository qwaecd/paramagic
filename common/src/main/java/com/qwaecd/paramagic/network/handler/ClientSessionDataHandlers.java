package com.qwaecd.paramagic.network.handler;

import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.packet.session.S2CSessionDataSyncPacket;
import com.qwaecd.paramagic.network.packet.session.S2CSessionStopPacket;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.client.ClientSession;
import com.qwaecd.paramagic.spell.client.ClientSessionManager;
import com.qwaecd.paramagic.spell.core.store.SessionDataStore;
import com.qwaecd.paramagic.spell.core.store.SessionDataSyncPayload;


@PlatformScope(PlatformScopeType.CLIENT)
public class ClientSessionDataHandlers {
    public static void syncSessionData(S2CSessionDataSyncPacket packet, NetworkContext context) {
        SessionDataSyncPayload syncData = packet.getSyncData();
        ClientSessionManager manager = ClientSessionManager.instance();
        ClientSession session = manager.getSession(syncData.sessionId);
        if (session == null) {
            return;
        }
        final SessionDataStore dataStore = session.getDataStore();
        syncData.getEntries().forEach(entry -> dataStore.setDataValueWithSeq(entry.dataId, entry.sequenceNumber, entry.value));
    }

    public static void stopSession(S2CSessionStopPacket packet, NetworkContext context) {
        ClientSessionManager manager = ClientSessionManager.instance();
        ClientSession session = manager.getSession(packet.getSessionId());
        if (session == null) {
            return;
        }
        session.handleServerStop(packet.getReason());
    }
}
