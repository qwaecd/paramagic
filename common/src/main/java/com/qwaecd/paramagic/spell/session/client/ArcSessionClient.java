package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.view.HybridCasterSource;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ArcSessionClient extends ClientSession {
    public ArcSessionClient(UUID sessionId, @Nonnull HybridCasterSource casterSource) {
        super(sessionId, casterSource);
    }

    @Override
    public void tick(float deltaTime) {
    }
}
