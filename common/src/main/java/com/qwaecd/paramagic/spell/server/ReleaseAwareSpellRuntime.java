package com.qwaecd.paramagic.spell.server;

public interface ReleaseAwareSpellRuntime extends SpellRuntime {
    void release(ServerSpellContext context);
}
