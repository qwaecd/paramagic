package com.qwaecd.paramagic.spell.session;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

@PlatformScope(PlatformScopeType.COMMON)
@SuppressWarnings("ClassCanBeRecord")
public class SpellSessionRef {
    public static final int PROTOCOL_VERSION = 1;
    public final UUID serverSessionId;
    public final UUID casterEntityUuid;

    public SpellSessionRef(UUID serverSessionId, UUID casterEntityUuid) {
        this.serverSessionId = serverSessionId;
        this.casterEntityUuid = casterEntityUuid;
    }

    public static SpellSessionRef empty() {
        return null;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(PROTOCOL_VERSION);
        buffer.writeUUID(this.serverSessionId);
        buffer.writeUUID(this.casterEntityUuid);
    }

    public static SpellSessionRef fromBuffer(FriendlyByteBuf buffer) {
        int protocolVersion = buffer.readInt();
        if (protocolVersion != PROTOCOL_VERSION) {
            throw new IllegalArgumentException("Unsupported protocol version: " + protocolVersion + ", expected: " + PROTOCOL_VERSION);
        }
        UUID serverSessionId = buffer.readUUID();
        UUID casterEntityUuid = buffer.readUUID();
        return new SpellSessionRef(serverSessionId, casterEntityUuid);
    }

    public SpellSessionRef copy() {
        return new SpellSessionRef(this.serverSessionId, this.casterEntityUuid);
    }
}
