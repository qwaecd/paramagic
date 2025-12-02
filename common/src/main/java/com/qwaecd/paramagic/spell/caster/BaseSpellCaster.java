package com.qwaecd.paramagic.spell.caster;


import java.util.UUID;

public abstract class BaseSpellCaster<T> implements SpellCaster<T> {
    protected final UUID casterId;

    protected BaseSpellCaster(UUID casterId) {
        this.casterId = casterId;
    }

    @Override
    public UUID getCasterId() {
        return this.casterId;
    }
}
