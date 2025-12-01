package com.qwaecd.paramagic.spell.caster;

import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;

import java.util.UUID;

public interface SpellCaster {
    UUID getCasterId();
    boolean canStartSession(Spell spell, ServerSessionManager manager);
}
