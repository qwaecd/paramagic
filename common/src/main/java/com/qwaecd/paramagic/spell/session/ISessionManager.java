package com.qwaecd.paramagic.spell.session;

import javax.annotation.Nullable;
import java.util.UUID;

public interface ISessionManager {
    @Nullable
    SpellSession getSession(UUID sessionId);
}
