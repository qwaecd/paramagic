package com.qwaecd.paramagic.spell.caster;


import java.util.UUID;

public abstract class BaseSpellCaster implements SpellCaster {
    protected final UUID casterId;
    private int mana = ManaAccess.DEFAULT_MAX_MANA;
    private int maxMana = ManaAccess.DEFAULT_MAX_MANA;

    protected BaseSpellCaster(UUID casterId) {
        this.casterId = casterId;
    }

    @Override
    public UUID getCasterId() {
        return this.casterId;
    }

    @Override
    public int getMana() {
        return this.mana;
    }

    @Override
    public int getMaxMana() {
        return this.maxMana;
    }

    @Override
    public void setMana(int mana) {
        this.mana = Math.max(0, Math.min(mana, getMaxMana()));
    }

    @Override
    public void setMaxMana(int maxMana) {
        this.maxMana = Math.max(0, maxMana);
        this.mana = Math.min(this.mana, this.maxMana);
    }
}
