package com.qwaecd.paramagic.spell.session;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import java.util.UUID;

@PlatformScope(PlatformScopeType.COMMON)
@SuppressWarnings("ClassCanBeRecord")
public class SpellSessionRef {
    public static final int PROTOCOL_VERSION = 1;
    public final UUID serverSessionId;
    public final UUID casterEntityUuid;
    public final int casterNetworkId;

    public SpellSessionRef(UUID serverSessionId, UUID casterEntityUuid, int entityNetworkId) {
        this.serverSessionId = serverSessionId;
        this.casterEntityUuid = casterEntityUuid;
        this.casterNetworkId = entityNetworkId;
    }

    @Nonnull
    public static SpellSessionRef fromSession(ServerSession session) {
        return new SpellSessionRef(
                session.sessionId,
                session.getCaster().getCasterId(),
                session.getCaster().getEntityNetworkId()
        );
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(PROTOCOL_VERSION);
        buffer.writeInt(this.casterNetworkId);
        buffer.writeUUID(this.serverSessionId);
        buffer.writeUUID(this.casterEntityUuid);
    }

    public static SpellSessionRef fromBuffer(FriendlyByteBuf buffer) {
        int protocolVersion = buffer.readInt();
        if (protocolVersion != PROTOCOL_VERSION) {
            throw new IllegalArgumentException("Unsupported protocol version: " + protocolVersion + ", expected: " + PROTOCOL_VERSION);
        }
        int casterNetworkId = buffer.readInt();
        UUID serverSessionId = buffer.readUUID();
        UUID casterEntityUuid = buffer.readUUID();
        return new SpellSessionRef(serverSessionId, casterEntityUuid, casterNetworkId);
    }

    public SpellSessionRef copy() {
        return new SpellSessionRef(this.serverSessionId, this.casterEntityUuid, this.casterNetworkId);
    }
}
