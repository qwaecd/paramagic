package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.session.store.SessionDataStore;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public interface ServerSessionView {
    UUID getSessionId();
    SpellCaster getCaster();
    ServerLevel getLevel();
    SessionDataStore getDataStore();
}
