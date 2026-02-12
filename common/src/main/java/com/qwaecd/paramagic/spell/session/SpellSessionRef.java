package com.qwaecd.paramagic.spell.session;

import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import java.util.UUID;

@PlatformScope(PlatformScopeType.COMMON)
@SuppressWarnings("ClassCanBeRecord")
public class SpellSessionRef implements IDataSerializable {
    public static final int PROTOCOL_VERSION = 1;
    public final UUID serverSessionId;
    public final UUID casterEntityUuid;
    public final int casterNetworkId;

    public SpellSessionRef(UUID serverSessionId, UUID casterEntityUuid, int entityNetworkId) {
        this.serverSessionId = serverSessionId;
        this.casterEntityUuid = casterEntityUuid;
        this.casterNetworkId = entityNetworkId;
    }

    public UUID getServerSessionId() {
        return serverSessionId;
    }

    public UUID getCasterEntityUuid() {
        return casterEntityUuid;
    }

    public int getCasterNetworkId() {
        return casterNetworkId;
    }

    @Nonnull
    public static SpellSessionRef fromSession(ServerSession session) {
        return new SpellSessionRef(
                session.sessionId,
                session.getCaster().getCasterId(),
                session.getCaster().getEntityNetworkId()
        );
    }

    @Override
    public void write(DataCodec codec) {
        codec.writeInt("protocol_version", PROTOCOL_VERSION);
        codec.writeInt("caster_network_id", this.casterNetworkId);
        codec.writeUUID("server_session_id", this.serverSessionId);
        codec.writeUUID("caster_entity_uuid", this.casterEntityUuid);
    }

    public static SpellSessionRef fromCodec(DataCodec codec) {
        int protocolVersion = codec.readInt("protocol_version");
        if (protocolVersion != PROTOCOL_VERSION) {
            throw new IllegalArgumentException("Unsupported protocol version: " + protocolVersion + ", expected: " + PROTOCOL_VERSION);
        }
        int casterNetworkId = codec.readInt("caster_network_id");
        UUID serverSessionId = codec.readUUID("server_session_id");
        UUID casterEntityUuid = codec.readUUID("caster_entity_uuid");
        return new SpellSessionRef(serverSessionId, casterEntityUuid, casterNetworkId);
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
